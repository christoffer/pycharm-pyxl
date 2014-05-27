package com.christofferklang.pyxl;

import com.intellij.lexer.FlexAdapter;

import java.io.Reader;

public class PyxlLexerAdapter extends FlexAdapter {
    public PyxlLexerAdapter() {
        super(new PyxlLexer((Reader) null));
    }
}
