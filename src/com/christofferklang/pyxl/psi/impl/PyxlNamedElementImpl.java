package com.christofferklang.pyxl.psi.impl;

import com.christofferklang.pyxl.PyxlReference;
import com.christofferklang.pyxl.psi.PyxlNamedElement;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.stubs.PyClassNameIndex;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class PyxlNamedElementImpl extends ASTWrapperPsiElement implements PyxlNamedElement {
    public PyxlNamedElementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        List<PyClass> navigationTargets = new ArrayList<PyClass>(PyClassNameIndex.find("x_base", getProject(), false));
        if(navigationTargets.size() > 1) {
            PyClass pyClass = navigationTargets.get(0);
        }
        return null;
//        PyxlReference pyxlReference = new PyxlReference(this);
//        return pyxlReference;
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        return this;
    }
}
