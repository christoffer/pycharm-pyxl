package com.christofferklang.pyxl;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.jetbrains.python.PyElementTypes;
import com.jetbrains.python.PyTokenTypes;
import com.jetbrains.python.PythonParserDefinition;
import com.jetbrains.python.lexer.PythonIndentingProcessor;
import com.jetbrains.python.parsing.*;
import com.jetbrains.python.psi.LanguageLevel;
import org.jetbrains.annotations.NotNull;

import java.io.Reader;

public class PyxlParserDefinition extends PythonParserDefinition {

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new PyxlIndentingLexer();
    }

    private class PyxlIndentingLexer extends PythonIndentingProcessor {
        public PyxlIndentingLexer() {
            super(new PyxlLexer((Reader) null), TokenSet.EMPTY);
        }
    }


    @NotNull
    public PsiParser createParser(Project project) {
        return new PyxlParser();
    }

    private class PyxlParser extends PyParser {
        protected ParsingContext createParsingContext(
                PsiBuilder builder, LanguageLevel languageLevel,
                StatementParsing.FUTURE futureFlag) {

            builder.setDebugMode(true);
            return new PyxlParsingContext(builder, languageLevel, futureFlag);
        }
    }

    private class PyxlParsingContext extends ParsingContext {
        private final PyxlExpressionParsing pyxlExpressionParser;

        public PyxlParsingContext(
                final PsiBuilder builder, LanguageLevel languageLevel,
                StatementParsing.FUTURE futureFlag) {
            super(builder, languageLevel, futureFlag);
            pyxlExpressionParser = new PyxlExpressionParsing(this);
        }

        public ExpressionParsing getExpressionParser() {
            return pyxlExpressionParser;
        }
    }

    private class PyxlExpressionParsing extends ExpressionParsing {
        public PyxlExpressionParsing(ParsingContext context) {
            super(context);
        }

        public boolean parsePrimaryExpression(boolean isTargetExpression) {
            boolean match = super.parsePrimaryExpression(isTargetExpression);
            if (!match) {
                final IElementType firstToken = myBuilder.getTokenType();
                if (firstToken == PyxlTokenTypes.TAGBEGIN) {
                    parsePyxlTag();
                    return true;
                }
            }
            return match;
        }

        /**
         * Parse the current pyxl tag.
         */
        private void parsePyxlTag() {
            final PsiBuilder.Marker pyxl = myBuilder.mark();

            // Consume tag beginning.
            myBuilder.advanceLexer();

            // Consume attributes.
            parsePyxlAttributes();

            if (myBuilder.getTokenType() == PyxlTokenTypes.TAGENDANDCLOSE) {
                // The tag was self-closed ( /> ).
                myBuilder.advanceLexer();
                pyxl.done(PyElementTypes.STRING_LITERAL_EXPRESSION);
                return;
            } else if (myBuilder.getTokenType() == PyxlTokenTypes.TAGEND) {
                // The tag has content!
                myBuilder.advanceLexer();

                // Parse content.
                while (!myBuilder.eof()) {
                    if (myBuilder.getTokenType() == PyxlTokenTypes.STRING) {
                        myBuilder.advanceLexer();
                    } else if (myBuilder.getTokenType() == PyxlTokenTypes.TAGBEGIN) {
                        // Another pyxl tag just got started.
                        parsePyxlTag();
                    } else if (myBuilder.getTokenType() == PyxlTokenTypes.TAGCLOSE) {
                        // The tag got closed by </tag>.
                        pyxl.done(PyElementTypes.STRING_LITERAL_EXPRESSION);
                        return;
                    }
                }
            }

            myBuilder.error("pyxl expected");
            pyxl.done(PyElementTypes.STRING_LITERAL_EXPRESSION);
        }

        /**
         * Parse as many attribute="value" pairs as possible.
         */
        private void parsePyxlAttributes() {
            // Parse the current attribute="value" pair, if any.
            if (myBuilder.getTokenType() == PyxlTokenTypes.ATTRNAME) {
                myBuilder.advanceLexer();
                if (myBuilder.getTokenType() == PyTokenTypes.EQ) {
                    myBuilder.advanceLexer();
                    if (myBuilder.getTokenType() == PyTokenTypes.ATTRVALUE) {
                        myBuilder.advanceLexer();

                        // Parse remaining attributes.
                        parsePyxlAttributes();
                        return;
                    }
                }

                myBuilder.error("pyxl attr expected");
            }
        }
    }
}
