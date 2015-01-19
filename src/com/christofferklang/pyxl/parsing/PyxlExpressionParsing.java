package com.christofferklang.pyxl.parsing;

import com.christofferklang.pyxl.PyxlElementTypes;
import com.christofferklang.pyxl.PyxlTokenTypes;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.jetbrains.python.PyElementTypes;
import com.jetbrains.python.PyTokenTypes;
import com.jetbrains.python.parsing.ExpressionParsing;
import com.jetbrains.python.parsing.ParsingContext;

class PyxlExpressionParsing extends ExpressionParsing {
    private static class PyxlParsingException extends Throwable {
        public PyxlParsingException() {
            super();
        }

        public PyxlParsingException(String message) {
            super(message);
        }
    }

    public PyxlExpressionParsing(ParsingContext context) {
        super(context);
    }

    public boolean parsePrimaryExpression(boolean isTargetExpression) {
        if(myBuilder.getTokenType() == PyxlTokenTypes.TAGBEGIN) {
            if(myBuilder.lookAhead(1) == PyxlTokenTypes.TAGNAME) {
                return parsePyxlTag();
            } else {
                return false;
            }
        } else {
            return super.parsePrimaryExpression(isTargetExpression);
        }
    }

    /**
     * Parse an entire pyxl tag like:
     * <tag attr="val">stuff <br /> other stuff</tag>
     */
    private boolean parsePyxlTag() {
        final PsiBuilder.Marker pyxlInstanceCall = myBuilder.mark();
        final PsiBuilder.Marker pyxlClassInitCall = myBuilder.mark();
        myBuilder.advanceLexer();

        final boolean isConditional = myBuilder.getTokenType() == PyxlTokenTypes.CONDITIONAL;

        String qualifiedName;
        try {
            qualifiedName = parseQualifiedPyxlTagName();
        } catch(PyxlParsingException e) {
            myBuilder.error(e.getMessage());
            pyxlClassInitCall.drop();
            pyxlInstanceCall.drop();
            return false;
        }

        final PsiBuilder.Marker initArgList = myBuilder.mark();
        parsePyxlAttributes();
        initArgList.done(PyElementTypes.ARGUMENT_LIST);

        // Proceed to parse Instance arguments (tag body)

        IElementType token = myBuilder.getTokenType();
        if(token == PyxlTokenTypes.TAGENDANDCLOSE) {
            // Self closed tag, equivalent to x_tag(<attributes here>)(<empty>)
            myBuilder.advanceLexer();
            pyxlClassInitCall.done(isConditional ? PyxlElementTypes.COND_TAG : PyxlElementTypes.PYXL_CLASS_INIT_CALL);

            // Mark as dummy (empty) arg list
            myBuilder.mark().done(PyxlElementTypes.ARGUMENT_LIST);
        } else if(token == PyxlTokenTypes.TAGEND) {
            // Tag has body, equivalent to x_tag(<attributes here>)( <body elements here >)
            myBuilder.advanceLexer();
            pyxlClassInitCall.done(isConditional ? PyxlElementTypes.COND_TAG : PyxlElementTypes.PYXL_CLASS_INIT_CALL);

            final PsiBuilder.Marker instanceArgList = myBuilder.mark();
            if(!parseTagBody()) {
                // Something went wrong when parsing the body, bail
                instanceArgList.done(PyxlElementTypes.ARGUMENT_LIST);
                pyxlInstanceCall.done(PyxlElementTypes.PYXL_INSTANCE_CALL);
                return false;
            }

            myBuilder.advanceLexer(); // Consume the closing "tag begin" (</) token.

            try {
                parseQualifiedPyxlTagName(qualifiedName, null);
            } catch(PyxlParsingException e) {
                myBuilder.error(String.format("Pyxl expected closing tag: </%s>", qualifiedName));
                instanceArgList.done(PyxlElementTypes.ARGUMENT_LIST);
                pyxlInstanceCall.done(PyxlElementTypes.PYXL_INSTANCE_CALL);
                return false;
            }

            if(myBuilder.getTokenType() == PyxlTokenTypes.TAGEND) {
                myBuilder.advanceLexer();
            } else {
                myBuilder.error("Pyxl expected >");
            }
            instanceArgList.done(PyxlElementTypes.ARGUMENT_LIST);
        }

        pyxlInstanceCall.done(PyxlElementTypes.PYXL_INSTANCE_CALL);
        return true;
    }

    /**
     * Parses a tag body
     *
     * @return true if successfully parsed, false if error.
     */
    private boolean parseTagBody() {
        IElementType token;

        // Parse content until we hit a closing tag
        // Chew of chunks of tokens inside the tag body until we hit a tag close
        while((token = myBuilder.getTokenType()) != PyxlTokenTypes.CLOSING_TAGBEGIN) {
            // Check for embedded Python chunks
            try {
                if(parseEmbeddedPythonExpression()) {
                    continue;
                }
            } catch(PyxlParsingException e) {
                return false;
            }

            if(token == PyxlTokenTypes.TAGBEGIN) {
                // Check for embedded Pyxl tags (and comments)
                parsePyxlTag();
            } else if(token == PyxlTokenTypes.STRING) {
                // Check for string literals inside the tag
                PsiBuilder.Marker stringLiteral = myBuilder.mark();
                myBuilder.advanceLexer();
                stringLiteral.done(PyElementTypes.STRING_LITERAL_EXPRESSION);
            } else {
                // Anything else is an error
                myBuilder.error(String.format("Pyxl encountered unexpected token: %s", token));
                return false;
            }
        }

        return true;
    }

    /**
     * Parses a pyxl tag name, including an optional list of module qualifiers.
     *
     * @return the full qualified name (e.g. "module1.module2.tag")
     * @throws PyxlParsingException if @requiredQualifiedName is non-null and doesn't match the parsed qualified name.
     */
    private String parseQualifiedPyxlTagName(String requiredQualifiedName,
                                             PsiBuilder.Marker tagStartMarker) throws PyxlParsingException {
        final IElementType token = myBuilder.getTokenType();

        String fullQualifiedName = myBuilder.getTokenText();

        // Module qualifiers are straight up python identifiers followed by a dot
        if(token == PyxlTokenTypes.TAGNAME_MODULE) {
            if(tagStartMarker == null) tagStartMarker = myBuilder.mark();
            PsiBuilder.Marker moduleExpression = myBuilder.mark();
            myBuilder.advanceLexer();
            moduleExpression.done(PyxlElementTypes.MODULE_REFERENCE);
            if(myBuilder.getTokenType() != PyTokenTypes.DOT) {
                throw new PyxlParsingException();
            } else {
                myBuilder.advanceLexer();
            }
            fullQualifiedName = fullQualifiedName + "." +
                    parseQualifiedPyxlTagName(null, tagStartMarker);
        } else if(token == PyxlTokenTypes.TAGNAME) {
            // pyxl expands <p><c /></p> to x_p()(x_c());
            // so in order to get the same semantics as the corresponding python would have, we fake a call to the init
            // function of the pyxl tag class here.

            if(tagStartMarker == null) tagStartMarker = myBuilder.mark();
            myBuilder.advanceLexer();
            tagStartMarker.done(PyxlElementTypes.PYCLASS_REF);
        } else if(token == PyxlTokenTypes.CONDITIONAL) {
            myBuilder.advanceLexer();
        } else {
            throw new PyxlParsingException();
        }

        if(requiredQualifiedName != null && !requiredQualifiedName.equals(fullQualifiedName)) {
            throw new PyxlParsingException("expected starting tag " + requiredQualifiedName);
        }

        return fullQualifiedName;
    }

    private String parseQualifiedPyxlTagName() throws PyxlParsingException {
        return parseQualifiedPyxlTagName(null, null);
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
    private boolean parseEmbeddedPythonExpression() throws PyxlParsingException {
        if(myBuilder.getTokenType() == PyxlTokenTypes.EMBED_START) {
            myBuilder.advanceLexer();
            super.parseExpression(); // parse as regular Python expression
            if(myBuilder.getTokenType() == PyxlTokenTypes.EMBED_END) {
                myBuilder.advanceLexer();
                return true;
            } else {
                myBuilder.error("Pyxl expected embed end");
                throw new PyxlParsingException();
            }
        }
        return false;
    }

    /**
     * Parse all pyxl tag attribute="value" pairs.
     */
    private void parsePyxlAttributes() {
        while(myBuilder.getTokenType() == PyxlTokenTypes.ATTRNAME) {
            final PsiBuilder.Marker attr = myBuilder.mark();
            final PsiBuilder.Marker attrName = myBuilder.mark();
            myBuilder.advanceLexer();
            attrName.done(PyxlElementTypes.ATTRNAME);

            if(myBuilder.getTokenType() == PyTokenTypes.EQ) {
                myBuilder.advanceLexer();
                if(parsePyxlAttributeValue()) {
                    attr.done(PyElementTypes.KEYWORD_ARGUMENT_EXPRESSION);
                } else {
                    // parsePyxlAttributeValue sets its own errors.
                    attr.done(PyElementTypes.KEYWORD_ARGUMENT_EXPRESSION);
                }
            } else {
                myBuilder.error("Pyxl expected =");
                attr.done(PyElementTypes.KEYWORD_ARGUMENT_EXPRESSION);
            }
        }
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
        if(token == PyxlTokenTypes.ATTRVALUE_START) {
            myBuilder.advanceLexer();
            attrStartToken = token;
        }

        boolean foundValue = false;
        while(true) {
            token = myBuilder.getTokenType();

            // Attempt to parse an embed expression.
            try {
                if(parseEmbeddedPythonExpression()) {
                    foundValue = true;
                    continue;
                }
            } catch(PyxlParsingException e) {
                break;
            }

            // Or consume literal attribute values.
            if(token == PyxlTokenTypes.ATTRVALUE) {
                foundValue = true;
                myBuilder.advanceLexer();
                continue;
            }

            if(attrStartToken != null) {
                if(token == PyxlTokenTypes.ATTRVALUE_END) {
                    myBuilder.advanceLexer();
                    return true;
                } else {
                    myBuilder.error("Pyxl expected attribute end");
                    return false;
                }
            } else if(foundValue) {
                return true;
            } else {
                myBuilder.error("Pyxl expected attribute value");
                return false;
            }
        }
        return false;
    }
}
