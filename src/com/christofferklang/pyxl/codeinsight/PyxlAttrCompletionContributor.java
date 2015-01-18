package com.christofferklang.pyxl.codeinsight;

import com.christofferklang.pyxl.psi.PyxlAttrName;
import com.christofferklang.pyxl.psi.PyxlTag;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.jetbrains.python.psi.*;
import org.jetbrains.annotations.NotNull;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class PyxlAttrCompletionContributor extends CompletionContributor {
    public static final java.lang.String PYXL_ATTR_TYPE_TEXT = "Pyxl Attribute";

    public PyxlAttrCompletionContributor() {
        extend(CompletionType.BASIC,
                psiElement().inside(PyxlAttrName.class),
                new PyxlAttrCompletionProvider());
    }

    private class PyxlAttrCompletionProvider extends CompletionProvider<CompletionParameters> {
        @Override
        protected void addCompletions(@NotNull CompletionParameters parameters,
                                      ProcessingContext context,
                                      @NotNull CompletionResultSet resultSet) {
            PyClass pyClass = getPyClass(parameters.getPosition());
            if(pyClass != null) {
                walkAndBuildAttrs(pyClass, resultSet, true);
                resultSet.stopHere(); // only attributes are allowed here
            }
        }

        private void walkAndBuildAttrs(PyClass pyClass, CompletionResultSet resultSet, boolean isFirstClass) {
            addAttrsOfClass(pyClass, resultSet, isFirstClass);
            for(PyClass parent : pyClass.getSuperClasses()) {
                walkAndBuildAttrs(parent, resultSet, false);
            }
        }

        private void addAttrsOfClass(PyClass pyClass, CompletionResultSet resultSet, boolean boldEntry) {
            PyTargetExpression attrsTargetExpr = pyClass.findClassAttribute("__attrs__", false);
            if(attrsTargetExpr == null) return;

            PyDictLiteralExpression attrsDict = (PyDictLiteralExpression) attrsTargetExpr.findAssignedValue();
            if(attrsDict == null) return;

            for(PyKeyValueExpression keyValue : attrsDict.getElements()) {
                PyExpression key = keyValue.getKey();
                if(key instanceof PyStringLiteralExpression) {
                    String name = ((PyStringLiteralExpression) key).getStringValue();
                    resultSet.addElement(
                            LookupElementBuilder.create(name)
                                    .withTypeText(PYXL_ATTR_TYPE_TEXT, true)
                                    .withTailText(" (" + pyClass.getName() + ")", true)
                                    .withBoldness(boldEntry)
                    );
                }
            }
        }
    }

    private PyClass getPyClass(PsiElement element) {
        PsiElement seeker = element;
        while(!(seeker instanceof PyxlTag) && seeker != null) {
            seeker = seeker.getParent();
        }
        if(seeker != null) {
            PyxlTag pyxlTag = (PyxlTag) seeker;
            return pyxlTag.getReferencedPythonClass();
        }
        return null;
    }
}
