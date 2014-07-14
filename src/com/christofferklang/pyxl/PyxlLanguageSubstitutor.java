package com.christofferklang.pyxl;

import com.intellij.lang.Language;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.LanguageSubstitutor;
import com.jetbrains.python.ReSTService;
import org.jetbrains.annotations.NotNull;

import java.io.*;

public class PyxlLanguageSubstitutor extends LanguageSubstitutor {
    @Override
    public Language getLanguage(@NotNull final VirtualFile vFile, @NotNull final Project project) {
        if(isPyxlFile(vFile)) {
            return PyxlLanguage.INSTANCE;
        }
        return null;
    }

    private boolean isPyxlFile(VirtualFile file) {

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            String firstLine = reader.readLine();
            return firstLine != null && firstLine.startsWith("# coding: pyxl");
        } catch (IOException e) {
            return false;
        }
    }
}
