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

import java.util.Arrays;
import java.util.List;

class PyxlExpressionParsing extends ExpressionParsing {
    private static class PyxlParsingException extends Throwable {
        public PyxlParsingException() {
            super();
        }

        public PyxlParsingException(String message) {
            super(message);
        }
    }

    private static final List<PyElementType> PYXL_CLOSE_TOKENS =
            Arrays.asList(PyxlTokenTypes.TAGCLOSE); // , PyxlTokenTypes.IFTAGCLOSE);

    public PyxlExpressionParsing(ParsingContext context) {
        super(context);
    }

    public boolean parsePrimaryExpression(boolean isTargetExpression) {
        if (myBuilder.getTokenType() == PyxlTokenTypes.TAGBEGIN) {
            parsePyxlTag();
            return true;
        } else {
            return super.parsePrimaryExpression(isTargetExpression);
        }
    }

    /**
     * Parse a pyxl tag.
     */
    private void parsePyxlTag() {
        final PsiBuilder.Marker pyxl = myBuilder.mark();
        myBuilder.advanceLexer();

        String qualifiedName;
        try {
            qualifiedName = parseQualifiedPyxlTagName();
        } catch (PyxlParsingException e) {
            myBuilder.error(e.getMessage());
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
                    PsiBuilder.Marker stringLiteral = myBuilder.mark();
                    myBuilder.advanceLexer();
                    stringLiteral.done(PyElementTypes.STRING_LITERAL_EXPRESSION);
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
                parseQualifiedPyxlTagName(qualifiedName, null, null);
            } catch (PyxlParsingException e) {
                pyxl.done(PyxlElementTypes.TAG);
                myBuilder.error(String.format("pyxl expected closing tag: </%s>", qualifiedName));
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
     * Parses a pyxl tag name, including an optional list of module qualifiers.
     *
     * @return the full qualified name (e.g. "module1.module2.tag")
     * @throws PyxlParsingException if @requiredQualifiedName is non-null and doesn't match the parsed qualified name.
     */
    private String parseQualifiedPyxlTagName(String requiredQualifiedName,
                                             PsiBuilder.Marker callExpressionMarker,
                                             PsiBuilder.Marker tagStartMarker) throws PyxlParsingException {
        final IElementType token = myBuilder.getTokenType();

        String fullQualifiedName = myBuilder.getTokenText();

        // Module qualifiers are straight up python identifiers followed by a dot
        if (token == PyxlTokenTypes.TAGNAME_MODULE) {
            if (callExpressionMarker == null) callExpressionMarker = myBuilder.mark();
            if (tagStartMarker == null) tagStartMarker = myBuilder.mark();
            PsiBuilder.Marker moduleExpression = myBuilder.mark();
            myBuilder.advanceLexer();
            moduleExpression.done(PyxlElementTypes.MODULE_REFERENCE);
            if (myBuilder.getTokenType() != PyTokenTypes.DOT) {
                throw new PyxlParsingException();
            } else {
                myBuilder.advanceLexer();
            }
            fullQualifiedName = fullQualifiedName + "." +
                    parseQualifiedPyxlTagName(null, callExpressionMarker, tagStartMarker);
        } else if (token == PyxlTokenTypes.TAGNAME) {
            // pyxl expands <p><c /></p> to x_p()(x_c());
            // so in order to get the same semantics as the corresponding python would have, we fake a call to the init
            // function of the pyxl tag class here.

            if (callExpressionMarker == null) callExpressionMarker = myBuilder.mark();
            if (tagStartMarker == null) tagStartMarker = myBuilder.mark();
            myBuilder.advanceLexer();
            tagStartMarker.done(PyxlElementTypes.TAG_REFERENCE);
            callExpressionMarker.done(PyElementTypes.CALL_EXPRESSION);
        } else if (token == PyxlTokenTypes.BUILT_IN_TAG) {
            myBuilder.advanceLexer();
        } else {
            throw new PyxlParsingException();
        }

        if (requiredQualifiedName != null && !requiredQualifiedName.equals(fullQualifiedName)) {
            throw new PyxlParsingException("expected starting tag " + requiredQualifiedName);
        }

        return fullQualifiedName;
    }

    private String parseQualifiedPyxlTagName() throws PyxlParsingException {
        return parseQualifiedPyxlTagName(null, null, null);
    }


    /**
     * Attempt to parse an embedded python expression. For example:
     * {self.counter + 1}. If an error occurs while the embedded expression
     * is being parsed a parse error will be set. It is ok to call this
     * method whenever a embedded python expression could occur, even if
     * the lexer isn't currently ready to produce one.
     *
     * @return true if an embedded expression was parsed, or false
     * otherwise.
     * @throws PyxlParsingException if an error occurs while the embedded
     *                              expression is being parsed.
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
     *
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
