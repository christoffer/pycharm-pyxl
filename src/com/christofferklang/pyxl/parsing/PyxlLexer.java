package com.christofferklang.pyxl.parsing;

import com.intellij.lexer.FlexAdapter;

import java.io.Reader;

public class PyxlLexer extends FlexAdapter {
    public PyxlLexer() {
        super(new _PyxlLexer((Reader) null));
    }
}
