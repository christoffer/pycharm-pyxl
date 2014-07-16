package com.christofferklang.pyxl.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import com.jetbrains.python.psi.impl.PyElementImpl;
import org.jetbrains.annotations.NotNull;

public class PyxlTagBeginImpl extends PyElementImpl {
    public PyxlTagBeginImpl(ASTNode astNode) {
        super(astNode);
    }

    @Override
    public String toString() {
        return "HOELSLAD:AS";
    }

    @NotNull
    @Override
    public PsiReference[] getReferences() {
        return super.getReferences();
    }

    @Override
    public String getText() {
        return "Hello";
    }
}
