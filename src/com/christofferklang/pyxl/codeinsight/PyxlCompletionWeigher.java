package com.christofferklang.pyxl.codeinsight;

import com.intellij.codeInsight.completion.CompletionLocation;
import com.intellij.codeInsight.completion.CompletionWeigher;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.psi.util.PsiUtilCore;
import com.jetbrains.python.PythonLanguage;
import org.jetbrains.annotations.NotNull;

public class PyxlCompletionWeigher extends CompletionWeigher {
    @Override
    public Comparable weigh(@NotNull final LookupElement element, @NotNull final CompletionLocation location) {
        if(!PsiUtilCore.findLanguageFromElement(location.getCompletionParameters().getPosition()).isKindOf(PythonLanguage.getInstance())) {
            return null;
        }

        final LookupElementPresentation presentation = LookupElementPresentation.renderElement(element);

        Boolean isImplicit = element.getUserData(PyxlClassCompletionContributor.IMPLICIT_LOOKUP);
        if(isImplicit != null && isImplicit) {
            return 1;
        }

        // Promote pyxl tags
        if(PyxlClassCompletionContributor.PYXL_TAG_TYPE_TEXT.equals(presentation.getTypeText()) ||
                PyxlAttrCompletionContributor.PYXL_ATTR_TYPE_TEXT.equals(presentation.getTypeText())) {
            return presentation.getItemText() == null ? 0 : presentation.getItemText().length();
        }
        return null; // default
    }
}
