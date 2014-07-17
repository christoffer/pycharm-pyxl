package com.christofferklang.pyxl;

import com.christofferklang.pyxl.psi.PyxlTagReference;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

public class PyxlTagReferenceProvider extends PsiReferenceProvider {
    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
        System.out.println("using my reference provider");
        if(element instanceof PyxlTagReference) {
            return new PsiReference[] { ((PyxlTagReference) element).getReference() };
        }

        return PsiReference.EMPTY_ARRAY;
    }

    @Override
    public boolean acceptsTarget(@NotNull PsiElement target) {
        return super.acceptsTarget(target);
    }
}
