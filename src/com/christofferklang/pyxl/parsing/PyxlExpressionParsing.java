package com.christofferklang.pyxl.parsing;

import com.christofferklang.pyxl.PyxlElementTypes;
import com.christofferklang.pyxl.PyxlTokenTypes;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.jetbrains.python.PyElementTypes;
import com.jetbrains.python.PyTokenTypes;
import com.jetbrains.python.parsing.ExpressionParsing;
import com.jetbrains.python.parsing.ParsingContext;
import com.jetbrains.python.psi.PyElementType;
import com.sun.javafx.beans.annotations.NonNull;

import java.util.Arrays;
import java.util.List;

class PyxlExpressionParsing extends ExpressionParsing {
    private static class PyxlParsingException extends Throwable {}

    private static class Token {
        public final IElementType type;
        public final String text;

        public Token(IElementType type, String text) {
            this.type = type;
            this.text = text;
        }

        @NonNull
        public String getTagName() {
            return String.format("%s", text);
        }

        public String toString() {
            return String.format("%s: %s", type, text);
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof Token) {
                Token other = (Token) obj;
                return other.type.equals(type) && other.getTagName().equals(getTagName());
            }
            return false;
        }
    }

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
     * Parse a pyxl tag.
     */
    private void parsePyxlTag() {
        final PsiBuilder.Marker pyxl = myBuilder.mark();
        myBuilder.advanceLexer();

        Token startToken;
        try {
            startToken = parsePyxlTagName();
        } catch (PyxlParsingException e) {
            myBuilder.error("pyxl expected starting tag");
            pyxl.done(PyxlElementTypes.TAG);
            return;
        }

        if (!parsePyxlAttributes()) {
            pyxl.done(PyxlElementTypes.TAG);
            return;
        }

        IElementType token = myBuilder.getTokenType();
        if (token == PyxlTokenTypes.TAGENDANDCLOSE) {
            // The tag was self-closed ( /> ).
            final PsiBuilder.Marker argumentList = myBuilder.mark();
            argumentList.done(PyxlElementTypes.ARGUMENT_LIST);
            myBuilder.advanceLexer();
        } else if (token == PyxlTokenTypes.TAGEND) {
            // The tag has content (even empty content counts).
            myBuilder.advanceLexer();

            final PsiBuilder.Marker argumentList = myBuilder.mark();

            // Parse pyxl tag content.
            boolean error = false;
            while ((token = myBuilder.getTokenType()) != PyxlTokenTypes.TAGCLOSE) {
                // Parse embed expressions of the form {python_code}.
                try {
                    if (parsePyxlEmbed()) {
                        continue;
                    }
                } catch (PyxlParsingException e) {
                    error = true;
                    break;
                }

                if (token == PyxlTokenTypes.TAGBEGIN) {
                    parsePyxlTag();
                } else if (token == PyxlTokenTypes.STRING) {
                    myBuilder.advanceLexer();
                } else {
                    myBuilder.error(String.format("pyxl encountered unexpected token: %s", token));
                    error = true;
                    break;
                }
            }

            argumentList.done(PyElementTypes.ARGUMENT_LIST);

            if (error) {
                pyxl.done(PyxlElementTypes.TAG);
                return;
            }

            // Consume the </ token.
            myBuilder.advanceLexer();

            try {
                parsePyxlTagName(startToken);
            } catch (PyxlParsingException e) {
                pyxl.done(PyxlElementTypes.TAG);
                myBuilder.error(String.format("pyxl expected closing tag: </%s>", startToken.getTagName()));
                return;
            }

            if (myBuilder.getTokenType() == PyxlTokenTypes.TAGEND) {
                myBuilder.advanceLexer();
            } else {
                myBuilder.error("pyxl expected >");
            }
        }
        pyxl.done(PyxlElementTypes.TAG);
    }

    /**
     * Parse the name of a pyxl tag, an if tag, or an else tag.
     * @return true if a tag name was parsed, or false otherwise.
     */
    private Token parsePyxlTagName(Token expectedToken) throws PyxlParsingException {
        final IElementType token = myBuilder.getTokenType();
        final String text = myBuilder.getTokenText();
        Token thisToken = new Token(token, text);

        if (expectedToken != null && !thisToken.equals(expectedToken)) {
            throw new PyxlParsingException();
        }

        if (token == PyxlTokenTypes.TAGNAME) {
            final PsiBuilder.Marker tag = myBuilder.mark();
            myBuilder.advanceLexer();
            tag.done(PyxlElementTypes.TAG_REFERENCE);
        } else if (token == PyxlTokenTypes.BUILT_IN_TAG ) {
            myBuilder.advanceLexer();
        } else {
            throw new PyxlParsingException();
        }

        return thisToken;
    }
    private Token parsePyxlTagName() throws PyxlParsingException {
        return parsePyxlTagName(null);
    }

    /**
     * Attempt to parse an embedded python expression. For example:
     * {self.counter + 1}. If an error occurs while the embedded expression
     * is being parsed a parse error will be set. It is ok to call this
     * method whenever a embedded python expression could occur, even if
     * the lexer isn't currently ready to produce one.
     * @return true if an embedded expression was parsed, or false
     * otherwise.
     * @throws PyxlParsingException if an error occurs while the embedded
     * expression is being parsed.
     */
    private boolean parsePyxlEmbed() throws PyxlParsingException {
        if (myBuilder.getTokenType() == PyxlTokenTypes.EMBED_START) {
            myBuilder.advanceLexer();
            parseExpression();
            if (myBuilder.getTokenType() == PyxlTokenTypes.EMBED_END) {
                myBuilder.advanceLexer();
                return true;
            } else {
                myBuilder.error("pyxl expected embed end");
                throw new PyxlParsingException();
            }
        }
        return false;
    }

    /**
     * Parse all pyxl tag attribute="value" pairs.
     */
    private boolean parsePyxlAttributes() {
        while (myBuilder.getTokenType() == PyxlTokenTypes.ATTRNAME) {
            final PsiBuilder.Marker attr = myBuilder.mark();
            final PsiBuilder.Marker attrName = myBuilder.mark();
            myBuilder.advanceLexer();
            attrName.done(PyxlElementTypes.ATTRNAME);

            if (myBuilder.getTokenType() == PyTokenTypes.EQ) {
                myBuilder.advanceLexer();
                if (parsePyxlAttributeValue()) {
                    attr.done(PyElementTypes.KEYWORD_ARGUMENT_EXPRESSION);

                    // Parse the next attribute="value" pair.
                    continue;
                } else {
                    // parsePyxlAttributeValue sets its own errors.
                    attr.done(PyElementTypes.KEYWORD_ARGUMENT_EXPRESSION);
                    return false;
                }
            } else {
                myBuilder.error("pyxl expected =");
                attr.done(PyElementTypes.KEYWORD_ARGUMENT_EXPRESSION);
                return false;
            }
        }
        return true;
    }

    /**
     * Parse a pyxl attribute value. If an error occurs while the attribute
     * value is being parsed a parse error will be set.
     * @return true if a value was successfully parsed and false
     * otherwise.
     */
    private boolean parsePyxlAttributeValue() {
        IElementType token = myBuilder.getTokenType();

        // Consume the start of an attribute.
        IElementType attrStartToken = null;
        if (token == PyxlTokenTypes.ATTRVALUE_START) {
            myBuilder.advanceLexer();
            attrStartToken = token;
        }

        boolean foundValue = false;
        while (true) {
            token = myBuilder.getTokenType();

            // Attempt to parse an embed expression.
            try {
                if (parsePyxlEmbed()) {
                    foundValue = true;
                    continue;
                }
            } catch (PyxlParsingException e) {
                break;
            }

            // Or consume literal attribute values.
            if (token == PyxlTokenTypes.ATTRVALUE) {
                foundValue = true;
                myBuilder.advanceLexer();
                continue;
            }

            if (attrStartToken != null) {
                if (token == PyxlTokenTypes.ATTRVALUE_END) {
                    myBuilder.advanceLexer();
                    return true;
                } else {
                    myBuilder.error("pyxl expected attribute end");
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
