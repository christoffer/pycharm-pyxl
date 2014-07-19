package com.christofferklang.pyxl;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.jetbrains.python.PythonFileType;
import com.jetbrains.python.psi.LanguageLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

public class PyxlColorSettingsPage implements ColorSettingsPage {
    private static final AttributesDescriptor[] COLOR_ATTRIBUTES = new AttributesDescriptor[]{
            new AttributesDescriptor("Tag name", PyxlHighlighterColors.PYXL_TAG_NAME),
            new AttributesDescriptor("Tag", PyxlHighlighterColors.PYXL_TAG),
            new AttributesDescriptor("Attribute name", PyxlHighlighterColors.PYXL_ATTRIBUTE_NAME),
            new AttributesDescriptor("Attribute value", PyxlHighlighterColors.PYXL_ATTRIBUTE_VALUE),
            new AttributesDescriptor("Embedded Python", PyxlHighlighterColors.PYXL_EMBEDDED),
    };

    @Nullable
    @Override
    public Icon getIcon() {
        return PythonFileType.INSTANCE.getIcon();
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new PyxlHighlighter(LanguageLevel.getDefault());
    }

    @NotNull
    @Override
    public String getDemoText() {
        return "# coding: pyxl\n" +
                "\n" +
                "from pyxl import html\n" +
                "\n" +
                "def generate():\n" +
                "	markup = (\n" +
                "		<html>\n" +
                "			<body class=\"the-body-class\" data-value=\"'value-{get_value()}\">\n" +
                "			Hello {user.get_name()}!\n" +
                "			</body>\n" +
                "		</html>\n" +
                "	)\n" +
                "	return <div class=\"wrapper\">{markup}</div>";
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
    }

    @NotNull
    @Override
    public AttributesDescriptor[] getAttributeDescriptors() {
        return COLOR_ATTRIBUTES;
    }

    @NotNull
    @Override
    public ColorDescriptor[] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Pyxl Colors";
    }
}
