package com.christofferklang.pyxl.parsing;

import com.intellij.lexer.FlexAdapter;

/**
 * Created by Christoffer on 7/19/2014.
 */
public class PyxlLexerAdapter extends FlexAdapter {
    public PyxlLexerAdapter() {
        super(new PyxlLexer());
    }
}
