package com.christofferklang.pyxl;

import com.christofferklang.pyxl.psi.PyxlTagReference;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.*;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.ProcessingContext;
import com.jetbrains.python.psi.PyCallExpression;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.impl.PyCallExpressionImpl;
import com.jetbrains.python.psi.impl.PyStringLiteralExpressionImpl;
import com.jetbrains.python.psi.stubs.PyClassNameIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PyxlTagReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(
                PlatformPatterns.psiElement(PyxlTokenTypes.TAGNAME),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                        return new PsiReference[] { new PyClassReference(element, element.getTextRange()) };
                    }
                },
                PsiReferenceRegistrar.DEFAULT_PRIORITY
        );
    }

    private class PyClassReference implements PsiReference {
        private final TextRange mTextRange;
        private final PsiElement mElement;

        public PyClassReference(PsiElement element, TextRange textRange) {
            mElement = element;
            mTextRange = textRange;
        }

        @Override
        public PsiElement getElement() {
            return mElement;
        }

        @Override
        public TextRange getRangeInElement() {
            return mTextRange;
        }

        @Nullable
        @Override
        public PsiElement resolve() {
            List<PyClass> result = new ArrayList<PyClass>(PyClassNameIndex.find("x_div",
                    mElement.getProject(), false));
            return result.get(0);
        }

        @NotNull
        @Override
        public String getCanonicalText() {
            return "CONAN";
        }

        @Override
        public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
            return null;
        }

        @Override
        public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
            return null;
        }

        @Override
        public boolean isReferenceTo(PsiElement element) {
            return resolve() == element;
        }

        @NotNull
        @Override
        public Object[] getVariants() {
            return new Object[0];
        }

        @Override
        public boolean isSoft() {
            return false;
        }
    }
}
