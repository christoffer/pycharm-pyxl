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
import com.jetbrains.python.parsing.ExpressionParsing;
import com.jetbrains.python.parsing.ParsingContext;
import com.jetbrains.python.parsing.PyParser;
import com.jetbrains.python.parsing.StatementParsing;
import com.jetbrains.python.psi.LanguageLevel;
import com.jetbrains.python.psi.PyElementType;
import org.jetbrains.annotations.NotNull;

import java.io.Reader;
import java.util.Arrays;
import java.util.List;

public class PyxlParserDefinition extends PythonParserDefinition {

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new PyxlIndentingLexer();
    }

    @NotNull
    public PsiParser createParser(Project project) {
        return new PyxlParser();
    }

    private class PyxlIndentingLexer extends PythonIndentingProcessor {
        public PyxlIndentingLexer() {
            super(new PyxlLexer((Reader) null), TokenSet.EMPTY);
        }
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

    private static class PyxlExpressionParsing extends ExpressionParsing {
        private static final List<PyElementType> PYXL_CLOSE_TOKENS =
                Arrays.asList(PyxlTokenTypes.TAGCLOSE); // , PyxlTokenTypes.IFTAGCLOSE);

        public PyxlExpressionParsing(ParsingContext context) {
            super(context);
        }

        public boolean parsePrimaryExpression(boolean isTargetExpression) {
            boolean match = super.parsePrimaryExpression(isTargetExpression);
            if (!match) {
                //noinspection SuspiciousMethodCalls
                if (myBuilder.getTokenType() == PyxlTokenTypes.TAGBEGIN) {
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
            myBuilder.advanceLexer();

            IElementType token = myBuilder.getTokenType();

            if (!parsePyxlTagName()) {
                myBuilder.error("pyxl expected starting tag");
                pyxl.done(PyxlElementTypes.PYXL_STATEMENT);
                return;
            }

            parsePyxlAttributes();
            token = myBuilder.getTokenType();

            if (token == PyxlTokenTypes.TAGENDANDCLOSE) {
                // The tag was self-closed ( /> ).
                myBuilder.advanceLexer();
            } else if (token == PyxlTokenTypes.TAGEND) {
                // The tag has content (even empty content counts).
                myBuilder.advanceLexer();

                // Parse pyxl tag content.
                while ((token = myBuilder.getTokenType()) != PyxlTokenTypes.TAGCLOSE) {
                    // Parse embed expressions of the form {python_code}.
                    if (parsePyxlEmbed() == null) {
                        pyxl.done(PyxlElementTypes.PYXL_STATEMENT);
                        return;
                    }

                    token = myBuilder.getTokenType();
                    if (token == PyxlTokenTypes.TAGBEGIN) {
                        parsePyxlTag();
                    } else if (token == PyxlTokenTypes.STRING) {
                        myBuilder.advanceLexer();
                    } else {
                        myBuilder.error("pyxl encountered unexpected token: " + token.toString());
                        pyxl.done(PyxlElementTypes.PYXL_STATEMENT);
                        return;
                    }
                }

                // Consume the </ token.
                myBuilder.advanceLexer();

                if (!parsePyxlTagName()) {
                    myBuilder.error("pyxl expected closing tag");
                    pyxl.done(PyxlElementTypes.PYXL_STATEMENT);
                    return;
                }

                if (myBuilder.getTokenType() == PyxlTokenTypes.TAGEND) {
                    myBuilder.advanceLexer();
                } else {
                    myBuilder.error("pyxl expected >");
                }
            }
            pyxl.done(PyxlElementTypes.PYXL_STATEMENT);
        }

        private boolean parsePyxlTagName() {
            final IElementType token = myBuilder.getTokenType();

            if (token == PyxlTokenTypes.TAGNAME) {
                final PsiBuilder.Marker endTag = myBuilder.mark();
                myBuilder.advanceLexer();
                endTag.done(PyxlElementTypes.PYXL_TAG_REFERENCE);
            } else if (token == PyxlTokenTypes.IFTAG || token == PyxlTokenTypes.ELSETAG) {
                myBuilder.advanceLexer();
            } else {
                return false;
            }
            return true;
        }

        /**
         * Attempt to parse a python expression embedded in {}. For example:
         * {self.counter + 1}
         * @return true if an embedded expression was parsed, or no embedded
         * expression could be found, or false if an embedded expression was
         * found but an error occurred while it was being parsed.
         */
        private Integer parsePyxlEmbed() {
            if (myBuilder.getTokenType() == PyxlTokenTypes.EMBED_START) {
                myBuilder.advanceLexer();
                parseExpression();
                if (myBuilder.getTokenType() == PyxlTokenTypes.EMBED_END) {
                    myBuilder.advanceLexer();
                    return 1;
                } else {
                    myBuilder.error("pyxl expected embed end");
                    return null;
                }
            }
            return 0;
        }

        /**
         * Parse as many attribute="value" pairs as possible.
         */
        private void parsePyxlAttributes() {
            while (myBuilder.getTokenType() == PyxlTokenTypes.ATTRNAME) {
                final PsiBuilder.Marker attr = myBuilder.mark();
                final PsiBuilder.Marker attrName = myBuilder.mark();
                myBuilder.advanceLexer();
                attrName.done(PyxlElementTypes.PYXL_ATTRNAME);

                if (myBuilder.getTokenType() == PyTokenTypes.EQ) {
                    myBuilder.advanceLexer();
                    if (parsePyxlAttributeValue()) {
                        attr.done(PyElementTypes.KEYWORD_ARGUMENT_EXPRESSION);

                        // Parse the next attribute="value" pair.
                        continue;
                    }
                } else {
                    myBuilder.error("pyxl expected =");
                }

                attr.done(PyElementTypes.KEYWORD_ARGUMENT_EXPRESSION);
            }
        }

        private boolean parsePyxlAttributeValue() {
            IElementType token = myBuilder.getTokenType();

            // Consume the start of an attribute.
            boolean expectAttributeEnd = false;
            if (token == PyxlTokenTypes.ATTRVALUE_START) {
                myBuilder.advanceLexer();
                expectAttributeEnd = true;
            }

            boolean foundValue = false;
            while (true) {
                token = myBuilder.getTokenType();

                // Attempt to parse an embed expression.
                Integer parsedEmbed = parsePyxlEmbed();
                if (parsedEmbed == null) {
                    // An error occurred parsing the embed expression.
                    break;
                } else if (parsedEmbed == 1) {
                    foundValue = true;
                    continue;
                }

                // Or consume literal attribute values.
                if (token == PyxlTokenTypes.ATTRVALUE) {
                    foundValue = true;
                    myBuilder.advanceLexer();
                    continue;
                }

                if (expectAttributeEnd) {
                    if (token == PyxlTokenTypes.ATTRVALUE_END) {
                        myBuilder.advanceLexer();
                        return true;
                    } else {
                        myBuilder.error("pyxl expected attribute value");
                        return false;
                    }
                } else if (foundValue) {
                    return true;
                } else {
                    myBuilder.error("pyxl expected attribute value");
                    return false;
                }
            }
            return false;
        }
    }
}
