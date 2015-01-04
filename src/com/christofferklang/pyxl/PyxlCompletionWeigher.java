package com.christofferklang.pyxl;

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
        if (!PsiUtilCore.findLanguageFromElement(location.getCompletionParameters().getPosition()).isKindOf(PythonLanguage.getInstance())) {
            return 0;
        }

        final String name = element.getLookupString();
        final LookupElementPresentation presentation = LookupElementPresentation.renderElement(element);
        // Promote pyxl tags
        if(PyxlClassCompletionContributor.PYXL_TAG_TYPE_TEXT.equals(presentation.getTypeText())) {
            return element.getLookupString().length();
        }

        return 0; // default
    }
}
