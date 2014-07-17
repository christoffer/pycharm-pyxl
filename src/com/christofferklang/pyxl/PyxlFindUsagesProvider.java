package com.christofferklang.pyxl;

import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordOccurrence;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.Processor;
import com.jetbrains.python.PyTokenTypes;
import com.jetbrains.python.findUsages.PythonFindUsagesProvider;
import org.jetbrains.annotations.NotNull;

public class PyxlFindUsagesProvider extends PythonFindUsagesProvider {

    public PyxlFindUsagesProvider() {
        super();
        System.out.println("Pyxl usage provide created");
    }

    @Override
    public WordsScanner getWordsScanner() {
        return new PyxlWordScanner();
    }

    @NotNull
    @Override
    public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        return super.getNodeText(element, useFullName);
    }

    private static class PyxlWordScanner extends DefaultWordsScanner {
        public PyxlWordScanner() {
            super(new PyxlLexerAdapter(),
                    TokenSet.create(PyTokenTypes.IDENTIFIER, PyxlTokenTypes.TAGNAME),
                    TokenSet.create(PyTokenTypes.END_OF_LINE_COMMENT),
                    PyTokenTypes.STRING_NODES);
        }
    }
}
