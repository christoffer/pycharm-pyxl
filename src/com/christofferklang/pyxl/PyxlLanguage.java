package com.christofferklang.pyxl;

import com.intellij.lang.Language;
import com.intellij.psi.templateLanguages.TemplateLanguage;

public class PyxlLanguage extends Language implements TemplateLanguage {
    public static final PyxlLanguage INSTANCE = new PyxlLanguage();

    protected PyxlLanguage() {
        super("Pyxl");
    }
}
