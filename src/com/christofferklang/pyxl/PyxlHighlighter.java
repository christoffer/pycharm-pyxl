package com.christofferklang.pyxl;

import com.christofferklang.pyxl.psi.PyxlTypes;
import com.intellij.ide.dnd.Highlighters;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.XmlHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class PyxlHighlighter extends SyntaxHighlighterBase {
    private static final Map<IElementType, TextAttributesKey> keys1;
    private static final Map<IElementType, TextAttributesKey> keys2;

    static {
        keys1 = new HashMap<IElementType, TextAttributesKey>();
        keys2 = new HashMap<IElementType, TextAttributesKey>();

        keys1.put(PyxlTypes.IDENTIFIER, XmlHighlighterColors.HTML_ATTRIBUTE_NAME);
        keys1.put(PyxlTypes.QUOTED_VALUE, XmlHighlighterColors.HTML_ATTRIBUTE_VALUE);

        keys1.put(PyxlTypes.TAG_NAME, XmlHighlighterColors.HTML_TAG_NAME);
        keys1.put(PyxlTypes.COMMENT, XmlHighlighterColors.HTML_COMMENT);
    }

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new PythonPyxlLexerAdapter();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        return pack(keys1.get(tokenType), keys2.get(tokenType));
    }
}
