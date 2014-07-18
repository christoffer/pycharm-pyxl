package com.christofferklang.pyxl;

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.XmlHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;

public class PyxlHighlighterColors {
    private PyxlHighlighterColors() {
        // Prevent instantiation
    }

    public static final TextAttributesKey PYXL_TAG_NAME = TextAttributesKey.createTextAttributesKey("PYXL_TAG_NAME", XmlHighlighterColors.HTML_TAG_NAME);
    public static final TextAttributesKey PYXL_TAG = TextAttributesKey.createTextAttributesKey("PYXL_TAG", XmlHighlighterColors.HTML_TAG);
    public static final TextAttributesKey PYXL_ATTRIBUTE_NAME = TextAttributesKey.createTextAttributesKey("PYXL_ATTRIBUTE_NAME", XmlHighlighterColors.HTML_ATTRIBUTE_NAME);
    public static final TextAttributesKey PYXL_ATTRIBUTE_VALUE = TextAttributesKey.createTextAttributesKey("PYXL_ATTRIBUTE_VALUE", XmlHighlighterColors.HTML_ATTRIBUTE_VALUE);
    public static final TextAttributesKey PYXL_EMBEDDED = TextAttributesKey.createTextAttributesKey("PYXL_EMBEDDED", DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR);
}
