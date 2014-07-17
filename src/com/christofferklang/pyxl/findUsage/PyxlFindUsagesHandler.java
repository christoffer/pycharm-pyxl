package com.christofferklang.pyxl.findUsage;

import com.intellij.psi.PsiElement;
import com.jetbrains.python.findUsages.PyClassFindUsagesHandler;
import com.jetbrains.python.psi.PyClass;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;

public class PyxlFindUsagesHandler extends PyClassFindUsagesHandler {
    private final PyClass mPyxlClass;

    public PyxlFindUsagesHandler(@NotNull PyClass pyxlClass) {
        super(pyxlClass);
        mPyxlClass = pyxlClass;
    }

    @Override
    protected Collection<String> getStringsToSearch(PsiElement element) {
        return super.getStringsToSearch(element);
    }
}
