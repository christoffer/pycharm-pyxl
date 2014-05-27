package com.christofferklang.pyxl;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.stubs.PyClassNameIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PyxlReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {
    private String key;

    public PyxlReference(@NotNull PsiElement element) {
        super(element);
    }

    public PyxlReference(PsiElement element, TextRange range) {
        super(element, range);
        key = element.getText().substring(range.getStartOffset(), range.getEndOffset());
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean b) {
        Project project = myElement.getProject();
        Collection<PyClass> pyClasses = PyClassNameIndex.find("x_" + key, project, false);
        List<ResolveResult> results = new ArrayList<ResolveResult>();
        for(PyClass pyClass : pyClasses) {
            results.add(new PsiElementResolveResult(pyClass));
        }
        return results.toArray(new ResolveResult[results.size()]);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        Project project = myElement.getProject();
        Collection<String> pyClassNames = PyClassNameIndex.allKeys(project);
        List<LookupElement> variants = new ArrayList<LookupElement>();
        for(final String pyClassName : pyClassNames) {
            if(pyClassName.startsWith("x_")) {
                PyClass pyClass = PyClassNameIndex.findClass(pyClassName, project);
                if(pyClass != null) {
                    variants.add(LookupElementBuilder.create(pyClass).
                                    withTypeText(pyClass.getContainingFile().getName())
                    );
                }
            }
        }
        return variants.toArray();
    }
}
