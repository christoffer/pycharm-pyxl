package com.christofferklang.pyxl;

import com.christofferklang.pyxl.parsing.PyxlHighlightingLexer;
import com.intellij.lexer.Lexer;
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
    private final LanguageLevel myLanguageLevel;

    public PyxlHighlighter(LanguageLevel languageLevel) {
        super(languageLevel);
        myLanguageLevel = languageLevel;
    }

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new PyxlHighlightingLexer(myLanguageLevel);
    }

    static {
        keys1 = new THashMap<IElementType, TextAttributesKey>();
        keys2 = new THashMap<IElementType, TextAttributesKey>();

        keys1.put(PyxlTokenTypes.TAGBEGIN, PyxlHighlighterColors.PYXL_TAG);
        keys1.put(PyxlTokenTypes.TAGCLOSE, PyxlHighlighterColors.PYXL_TAG);
        keys1.put(PyxlTokenTypes.TAGEND, PyxlHighlighterColors.PYXL_TAG);
        keys1.put(PyxlTokenTypes.TAGENDANDCLOSE, PyxlHighlighterColors.PYXL_TAG);

        keys1.put(PyxlElementTypes.TAG, PyxlHighlighterColors.PYXL_EMBEDDED);

        keys1.put(PyxlTokenTypes.TAGNAME, PyxlHighlighterColors.PYXL_TAG_NAME);
        keys1.put(PyxlTokenTypes.TAGNAME_MODULE, PyxlHighlighterColors.PYXL_TAG_NAME);
        keys1.put(PyxlTokenTypes.BUILT_IN_TAG, PyxlHighlighterColors.PYXL_TAG_NAME);

        keys1.put(PyxlTokenTypes.ATTRNAME, PyxlHighlighterColors.PYXL_ATTRIBUTE_NAME);
        keys1.put(PyxlTokenTypes.ATTRVALUE, PyxlHighlighterColors.PYXL_ATTRIBUTE_VALUE);
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
