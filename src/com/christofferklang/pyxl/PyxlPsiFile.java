package com.christofferklang.pyxl;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;

public class PyxlPsiFile extends PsiFileBase {
    public PyxlPsiFile(FileViewProvider fileViewProvider) {
        super(fileViewProvider, PyxlLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return PyxlFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "Pyxl: " + getName();
    }
}
