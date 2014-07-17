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
        private static final List<PyElementType> PYXL_BEGIN_TOKENS =
                Arrays.asList(PyxlTokenTypes.TAGBEGIN, PyxlTokenTypes.IFTAG);

        private static final List<PyElementType> PYXL_CLOSE_TOKENS =
                Arrays.asList(PyxlTokenTypes.TAGCLOSE); // , PyxlTokenTypes.IFTAGCLOSE);

        public PyxlExpressionParsing(ParsingContext context) {
            super(context);
        }

        public boolean parsePrimaryExpression(boolean isTargetExpression) {
            boolean match = super.parsePrimaryExpression(isTargetExpression);
            if (!match) {
                //noinspection SuspiciousMethodCalls
                if (PYXL_BEGIN_TOKENS.contains(myBuilder.getTokenType())) {
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

            // Consume either < or <if>
            myBuilder.advanceLexer();

            // Consume the tag_name in <tag_name
            if (myBuilder.getTokenType() == PyxlTokenTypes.TAGNAME) {
                consumeTokenAsPyxlTag();
            }

            // Consume attributes.
            parsePyxlAttributes();

            if (myBuilder.getTokenType() == PyxlTokenTypes.TAGENDANDCLOSE) {
                // The tag was self-closed ( /> ).
                myBuilder.advanceLexer();
                pyxl.done(PyxlElementTypes.PYXL_STATEMENT);
                return;
            } else if (myBuilder.getTokenType() == PyxlTokenTypes.TAGEND) {
                // The tag has content (even empty content counts).
                myBuilder.advanceLexer();

                // Parse content.
                while (!myBuilder.eof()) {
                    // Parse embed expressions of the form {python_code}.
                    if (parsePyxlEmbed() == null) {
                        break;
                    }

                    if (myBuilder.getTokenType() == PyxlTokenTypes.STRING) {
                        myBuilder.advanceLexer();
                    } else if (PYXL_BEGIN_TOKENS.contains(myBuilder.getTokenType())) {
                        // Another pyxl tag just got started.
                        parsePyxlTag();
                    } else if (PYXL_CLOSE_TOKENS.contains(myBuilder.getTokenType())) {
                        // The tag got closed by </tag>.
                        consumeTokenAsPyxlTag();
                        pyxl.done(PyxlElementTypes.PYXL_STATEMENT);
                        return;
                    } else {
                        myBuilder.advanceLexer();
                    }
                }
            }

            myBuilder.error("pyxl expected");
            pyxl.done(PyxlElementTypes.PYXL_STATEMENT);
        }

        private void consumeTokenAsPyxlTag() {
            final PsiBuilder.Marker endTag = myBuilder.mark();
            myBuilder.advanceLexer();
            endTag.done(PyxlElementTypes.PYXL_TAG_PY_REFERENCE);
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
                myBuilder.advanceLexer();

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
