package com.christofferklang.pyxl;

import com.intellij.lexer.FlexAdapter;

import java.io.Reader;

public class PythonPyxlLexerAdapter extends FlexAdapter {
    public PythonPyxlLexerAdapter() {
        super(new PythonPyxlLexer((Reader) null));
    }
}
