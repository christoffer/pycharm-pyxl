package com.christofferklang.pyxl;

import com.christofferklang.pyxl.psi.PyxlTypes;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.LanguageParserDefinitions;
import com.intellij.lang.ParserDefinition;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.LanguageSubstitutors;
import com.intellij.psi.MultiplePsiFilesPerDocumentFileViewProvider;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.source.DummyHolderElement;
import com.intellij.psi.impl.source.PsiFileImpl;
import com.intellij.psi.templateLanguages.TemplateDataElementType;
import com.intellij.psi.templateLanguages.TemplateDataLanguageMappings;
import com.intellij.psi.templateLanguages.TemplateLanguageFileViewProvider;
import com.intellij.psi.tree.IElementType;
import com.jetbrains.python.PyElementTypes;
import com.jetbrains.python.PyTokenTypes;
import com.jetbrains.python.PythonLanguage;
import com.jetbrains.python.psi.PyElementType;
import gnu.trove.THashSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Set;

public class PyxlFileViewProvider
        extends MultiplePsiFilesPerDocumentFileViewProvider
        implements TemplateLanguageFileViewProvider {

    // Element used to signal parts of the "outer language"
    private static final IElementType OUTER_ELEMENT_TYPE = new PyxlElementType("Outer");

    public static final TemplateDataElementType PYTHON_TEMPLATE_DATA =
            new PyxlTemplateDataElementType(
                    "PYTHON_TEMPLATE_DATA",
                    PyxlLanguage.INSTANCE,
                    PyxlTypes.PYTHON_TEXT,
                    OUTER_ELEMENT_TYPE);

    private final Language mTemplateLanguage;

    public PyxlFileViewProvider(PsiManager manager, VirtualFile virtualFile, boolean physical) {
        super(manager, virtualFile, physical);
        mTemplateLanguage = getTemplateDataLanguage(manager, virtualFile);
    }

    private Language getTemplateDataLanguage(PsiManager manager, VirtualFile virtualFile) {
        Language dataLang = TemplateDataLanguageMappings.getInstance(manager.getProject()).getMapping(virtualFile);
        if (dataLang == null) {
            dataLang = PythonLanguage.getInstance();
        }

        Language substituteLang = LanguageSubstitutors.INSTANCE.substituteLanguage(dataLang, virtualFile, manager.getProject());

        // only use a substituted language if it's templateable
        if (TemplateDataLanguageMappings.getTemplateableLanguages().contains(substituteLang)) {
            dataLang = substituteLang;
        }

        return dataLang;
    }

    @NotNull
    @Override
    public Language getBaseLanguage() {
        // Base language is always pyxl
        return PyxlLanguage.INSTANCE;
    }

    @NotNull
    @Override
    public Language getTemplateDataLanguage() {
        // Template language is always going to be Python
        return PythonLanguage.getInstance();
    }

    @NotNull
    @Override
    public Set<Language> getLanguages() {
        return new THashSet<Language>(Arrays.asList(new Language[]{
                PyxlLanguage.INSTANCE, PythonLanguage.getInstance()
        }));
    }

    @Override
    protected MultiplePsiFilesPerDocumentFileViewProvider cloneInner(VirtualFile virtualFile) {
        return new PyxlFileViewProvider(getManager(), virtualFile, false);
    }

    @Nullable
    @Override
    protected PsiFile createFile(@NotNull Language language) {
        ParserDefinition parserDef = LanguageParserDefinitions.INSTANCE.forLanguage(language);
        if(parserDef == null) {
            return null;
        }

        if (language == PythonLanguage.getInstance()) {
            PsiFileImpl file = (PsiFileImpl) parserDef.createFile(this);
            file.setContentElementType(PYTHON_TEMPLATE_DATA);
            return file;
        } else if (language == PyxlLanguage.INSTANCE) {
            return LanguageParserDefinitions.INSTANCE.forLanguage(language).createFile(this);
        }
        return null;
    }

    public static class PythonDummyExpression extends com.jetbrains.python.psi.impl.PyElementImpl {
        public PythonDummyExpression() {
            super(new DummyHolderElement("()"));
        }

        public PythonDummyExpression(ASTNode astNode) {
            super(astNode);
        }
    }

    private static class PyxlTemplateDataElementType extends TemplateDataElementType {
        public PyxlTemplateDataElementType(String python_template_data, PyxlLanguage instance, IElementType templateElementType, IElementType outerElementType) {
            super(python_template_data, instance, templateElementType, outerElementType);
        }

//        @Override
//        protected OuterLanguageElementImpl createOuterLanguageElement(Lexer lexer, CharTable table, IElementType outerElementType) {
//            int start = lexer.getTokenStart();
//            int end = lexer.getTokenEnd();
//            if(end < start) {
//                end = start;
//            }
//            final CharSequence buffer = lexer.getBufferSequence();
//
//            return new OuterLanguageElementImpl(outerElementType, table.intern(buffer, start, end));
//        }
    }
}
