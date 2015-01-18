package com.christofferklang.pyxl.codeinsight;


import com.christofferklang.pyxl.PyxlTokenTypes;
import com.christofferklang.pyxl.psi.Helpers;
import com.christofferklang.pyxl.psi.PythonClassReference;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ProcessingContext;
import com.jetbrains.python.PythonLanguage;
import com.jetbrains.python.psi.*;
import com.jetbrains.python.psi.search.PyProjectScopeBuilder;
import com.jetbrains.python.psi.stubs.PyClassNameIndex;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class PyxlClassCompletionContributor extends CompletionContributor {
    public static final String PYXL_TAG_TYPE_TEXT = "Pyxl Tag";

    public static final Key<Boolean> IMPLICIT_LOOKUP = Key.create("implicit");

    public PyxlClassCompletionContributor() {
        extend(CompletionType.BASIC,
                psiElement().withLanguage(PythonLanguage.getInstance()),
                new PyxlCompletionProvider());
    }

    @Override
    public void fillCompletionVariants(CompletionParameters parameters, CompletionResultSet result) {
        final PsiElement element = parameters.getPosition();
        final PsiElement parent = element.getParent();
        if(parent instanceof PythonClassReference) {
            super.fillCompletionVariants(parameters, result);
        }
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
            if(!(targetFile instanceof PyFile)) {
                return;
            }
            final PyFile pyFile = (PyFile) targetFile;

            for(PyClass pyClass : getPyxlClassesInScope(pyFile)) {
                final String tagName = Helpers.pythonClassToPyxl(pyClass.getName());
                resultSet.addElement(PrioritizedLookupElement.withGrouping(
                                getLookupElementForString(tagName).withBoldness(true),
                                1)
                );
            }

            if(Helpers.getImportedPyxlHtmlModuleElementFromFile(pyFile) != null) {
                // Add default Pyxl/html tags if this file contains pyxl.html
                for(String defaultTag : Helpers.PYXL_TAG_NAMES) {
                    final String tagName = Helpers.pythonClassToPyxl(defaultTag);
                    LookupElementBuilder lookupElement = getLookupElementForString(tagName);
                    lookupElement = lookupElement.withTailText(" (pyxl.html)", true);
                    lookupElement.putUserData(IMPLICIT_LOOKUP, Boolean.TRUE);
                    resultSet.addElement(PrioritizedLookupElement.withGrouping(lookupElement, 2));
                }
            }
        }

        /**
         * Returns a list of strings for all the PyxlClassNames in the current scope.
         */
        public static List<PyClass> getPyxlClassesInScope(PyFile pyFile) {
            ArrayList<PyClass> pyxlClassNames = new ArrayList<PyClass>();

            for(PyClass pyClass : pyFile.getTopLevelClasses()) {
                final String className = pyClass.getName();
                if(className == null || !className.startsWith("x_")) {
                    continue;
                }

                pyxlClassNames.add(pyClass);
            }

            for(PyImportStatementBase importStatement : pyFile.getImportBlock()) {
                for(PyImportElement importElement : importStatement.getImportElements()) {
                    try {
                        if(!importElement.getImportedQName().getLastComponent().startsWith("x_")) {
                            continue; // Ignore non-pyxl stuff
                        }
                    } catch(NullPointerException ex) {
                        continue;
                    }

                    PsiElement resolvedElement = importElement.resolve();
                    if(resolvedElement instanceof PyClass) {
                        pyxlClassNames.add((PyClass) resolvedElement);
                    }
                }
            }

            return pyxlClassNames;
        }

        private static LookupElementBuilder getLookupElementForString(String tagName) {
            return LookupElementBuilder.create(tagName)
                    .withTypeText(PYXL_TAG_TYPE_TEXT, true);
        }

        @Override
        protected void addCompletions(@NotNull CompletionParameters parameters,
                                      ProcessingContext context,
                                      @NotNull CompletionResultSet resultSet) {

            final IElementType elementType = parameters.getPosition().getNode().getElementType();

            // We only care about completing Pyxl tags when a Pyxl tag is expected
            if(elementType == PyxlTokenTypes.TAGNAME || elementType == PyxlTokenTypes.TAGBEGIN) {
                final PsiFile originalFile = parameters.getOriginalFile();

                addCompletionsFromFile(originalFile, resultSet);
                if(parameters.isExtendedCompletion()) {
                    addCompletionsFromProject(originalFile, resultSet);
                }
                resultSet.stopHere(); // nothing else is allowed when a Pyxl tag is expected
            }
        }

        private void addCompletionsFromProject(PsiFile originalFile, CompletionResultSet resultSet) {
            final Project project = originalFile.getProject();
            final GlobalSearchScope scope = PyProjectScopeBuilder.excludeSdkTestsScope(originalFile);

            final Collection<String> keys = StubIndex.getInstance().getAllKeys(
                    PyClassNameIndex.KEY, project
            );

            for(final String elementName : CompletionUtil.sortMatching(resultSet.getPrefixMatcher(), keys)) {
                if(!elementName.startsWith("x_")) {
                    continue; // we only care about pyxl completions
                }

                for(PsiNamedElement element : StubIndex.getElements(PyClassNameIndex.KEY, elementName, project, scope, PyClass.class)) {
                    if(IS_TOPLEVEL.value(element)) {
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
