package com.christofferklang.pyxl.parsing;

import com.intellij.psi.tree.TokenSet;
import com.jetbrains.python.PyTokenTypes;
import com.jetbrains.python.lexer.PythonIndentingProcessor;

import java.io.Reader;

public class PyxlIndentingLexer extends PythonIndentingProcessor {
    public PyxlIndentingLexer() {
        super(new _PyxlLexer((Reader) null), TokenSet.EMPTY);
    }

    boolean addFinalBreak = true;

    protected void processSpecialTokens() {
        super.processSpecialTokens();
        int tokenStart = getBaseTokenStart();
        if(getBaseTokenType() == null && addFinalBreak) {
            pushToken(PyTokenTypes.STATEMENT_BREAK, tokenStart, tokenStart);
            addFinalBreak = false;
        }
    }
}
