package com.christofferklang.pyxl.codeinsight;


import com.christofferklang.pyxl.PyxlTokenTypes;
import com.christofferklang.pyxl.psi.Helpers;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ProcessingContext;
import com.jetbrains.python.PythonLanguage;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyFile;
import com.jetbrains.python.psi.PyUtil;
import com.jetbrains.python.psi.search.PyProjectScopeBuilder;
import com.jetbrains.python.psi.stubs.PyClassNameIndex;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class PyxlClassCompletionContributor extends CompletionContributor {
    public static final String PYXL_TAG_TYPE_TEXT = "Pyxl Tag";

    public PyxlClassCompletionContributor() {
        extend(CompletionType.BASIC,
                psiElement().withLanguage(PythonLanguage.getInstance()),
                new PyxlCompletionProvider());
    }

    @Override
    public void fillCompletionVariants(CompletionParameters parameters, CompletionResultSet result) {
        super.fillCompletionVariants(parameters, result);
    }

    private static class PyxlCompletionProvider extends CompletionProvider<CompletionParameters> {

        public static final Condition<PsiElement> IS_TOPLEVEL = new Condition<PsiElement>() {
            @Override
            public boolean value(PsiElement element) {
                return PyUtil.isTopLevel(element);
            }
        };

        /**
         * Add the Pyxl classes that are available in the file itself.
         */
        private static void addCompletionsFromFile(PsiFile targetFile, CompletionResultSet resultSet) {
            if (!(targetFile instanceof PyFile)) {
                return;
            }
            final PyFile pyFile = (PyFile) targetFile;

            for (PyClass pyClass : pyFile.getTopLevelClasses()) {
                final String className = pyClass.getName();
                if(className == null || !className.startsWith("x_")) {
                    continue;
                }

                final String tagName = Helpers.pyxlNameToTagName(className);
                resultSet.addElement(getLookupElementForString(tagName));
            }

            if(Helpers.getImportedPyxlHtmlModuleElementFromFile(pyFile) != null) {
                // Add default Pyxl/html tags if this file contains pyxl.html
                for (String defaultTag : Helpers.PYXL_TAG_NAMES) {
                    final String tagName = Helpers.pyxlNameToTagName(defaultTag);
                    LookupElementBuilder lookupElement = getLookupElementForString(tagName);
                    lookupElement = lookupElement.withTailText(" (pyxl.html)", true);
                    resultSet.addElement(lookupElement);
                }
            }
        }

        private static LookupElementBuilder getLookupElementForString(String tagName) {
            return LookupElementBuilder.create(tagName)
                    .withTypeText(PYXL_TAG_TYPE_TEXT, true)
                    .withBoldness(true);
        }

        @Override
        protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet resultSet) {
            final PsiElement parent = parameters.getPosition().getParent();
            IElementType elementType = parameters.getPosition().getNode().getElementType();
            if (elementType == PyxlTokenTypes.TAGNAME || elementType == PyxlTokenTypes.TAGBEGIN) {
                final PsiFile originalFile = parameters.getOriginalFile();

                if (parameters.isExtendedCompletion()) {
                    addCompletionsFromProject(originalFile, resultSet);
                }
                addCompletionsFromFile(originalFile, resultSet);
            }
        }

        private void addCompletionsFromProject(PsiFile originalFile, CompletionResultSet resultSet) {
            final Project project = originalFile.getProject();
            final GlobalSearchScope scope = PyProjectScopeBuilder.excludeSdkTestsScope(originalFile);

            final Collection<String> keys = StubIndex.getInstance().getAllKeys(
                    PyClassNameIndex.KEY, project
            );

            for (final String elementName : CompletionUtil.sortMatching(resultSet.getPrefixMatcher(), keys)) {
                if (!elementName.startsWith("x_")) {
                    continue; // we only care about pyxl completions
                }

                for (PsiNamedElement element : StubIndex.getElements(PyClassNameIndex.KEY, elementName, project, scope, PyClass.class)) {
                    if (IS_TOPLEVEL.value(element)) {
                        final String tagName = elementName.substring(2, elementName.length());
                        LookupElementBuilder lookupElement = LookupElementBuilder.create(tagName)
                                .withTailText(" " + ((NavigationItem) element).getPresentation().getLocationString(), true)
                                .withTypeText(PYXL_TAG_TYPE_TEXT, true);
                        resultSet.addElement(lookupElement);
                    }
                }
            }
        }
    }
}
