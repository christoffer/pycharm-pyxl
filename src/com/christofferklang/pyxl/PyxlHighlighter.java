package com.christofferklang.pyxl;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.XmlHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import com.jetbrains.python.highlighting.PyHighlighter;
import com.jetbrains.python.psi.LanguageLevel;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

class PyxlHighlighter extends PyHighlighter {
    private static final Map<IElementType, TextAttributesKey> keys1;
    private static final Map<IElementType, TextAttributesKey> keys2;

    public PyxlHighlighter(LanguageLevel languageLevel) {
        super(languageLevel);
    }

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new PyxlLexerAdapter();
    }

    static {
        keys1 = new THashMap<IElementType, TextAttributesKey>();
        keys2 = new THashMap<IElementType, TextAttributesKey>();

        keys1.put(PyxlTokenTypes.TAGBEGIN, XmlHighlighterColors.HTML_TAG_NAME);
        keys1.put(PyxlTokenTypes.TAGCLOSE, XmlHighlighterColors.HTML_TAG_NAME);
        keys1.put(PyxlTokenTypes.TAGEND, XmlHighlighterColors.HTML_TAG_NAME);
        keys1.put(PyxlTokenTypes.TAGENDANDCLOSE, XmlHighlighterColors.HTML_TAG_NAME);

        keys1.put(PyxlTokenTypes.ATTRNAME, XmlHighlighterColors.HTML_ATTRIBUTE_NAME);
        keys1.put(PyxlTokenTypes.ATTRVALUE, XmlHighlighterColors.HTML_ATTRIBUTE_VALUE);
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        TextAttributesKey[] defaultTextAttributeKeys, extendedHighlighterKeys;

        defaultTextAttributeKeys = super.getTokenHighlights(tokenType);
        extendedHighlighterKeys = SyntaxHighlighterBase.pack(keys1.get(tokenType), keys2.get(tokenType));

        int numKeys = defaultTextAttributeKeys.length + extendedHighlighterKeys.length;
        TextAttributesKey[] merged = new TextAttributesKey[numKeys];

        System.arraycopy(defaultTextAttributeKeys, 0, merged, 0, defaultTextAttributeKeys.length);
        System.arraycopy(extendedHighlighterKeys,
                0, merged,
                defaultTextAttributeKeys.length,
                extendedHighlighterKeys.length);

        return merged;
    }
}
