package com.christofferklang.pyxl;

import com.christofferklang.pyxl.findUsage.PyxlFindUsagesHandler;
import com.intellij.find.findUsages.FindUsagesHandler;
import com.intellij.find.findUsages.FindUsagesHandlerFactory;
import com.intellij.psi.PsiElement;
import com.jetbrains.python.findUsages.PyClassFindUsagesHandler;
import com.jetbrains.python.findUsages.PyFindUsagesHandlerFactory;
import com.jetbrains.python.psi.PyClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PyxlFindUsagesHandlerFactory extends PyFindUsagesHandlerFactory {
    @Nullable
    @Override
    public FindUsagesHandler createFindUsagesHandler(@NotNull PsiElement element, boolean forHighlightUsages) {
        FindUsagesHandler usageHandler = super.createFindUsagesHandler(element, forHighlightUsages);
        if(usageHandler instanceof PyClassFindUsagesHandler && isPyxlClass(element)) {
            return new PyxlFindUsagesHandler((PyClass) element);
        }
        return usageHandler;
    }

    private boolean isPyxlClass(PsiElement element) {
        return element instanceof PyClass && String.format("%s", ((PyClass) element).getName()).startsWith("x_");
    }
}
