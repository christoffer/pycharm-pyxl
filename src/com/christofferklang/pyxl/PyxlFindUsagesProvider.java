package com.christofferklang.pyxl;

import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import com.jetbrains.python.PyTokenTypes;
import com.jetbrains.python.findUsages.PythonFindUsagesProvider;
import org.jetbrains.annotations.NotNull;

public class PyxlFindUsagesProvider extends PythonFindUsagesProvider {

    public PyxlFindUsagesProvider() {
        super();
        System.out.println("Pyxl usage provide created");
    }

    @NotNull
    @Override
    public String getType(@NotNull PsiElement element) {
        String type = super.getType(element);
        System.out.println(type + " for " + element);
        return super.getType(element);
    }

    @Override
    public WordsScanner getWordsScanner() {
        return new PyxlWordScanner();
    }

    private static class PyxlWordScanner extends DefaultWordsScanner {
        public PyxlWordScanner() {
            super(new PyxlLexerAdapter(),
                    TokenSet.create(PyTokenTypes.IDENTIFIER),
                    TokenSet.create(PyTokenTypes.END_OF_LINE_COMMENT),
                    PyTokenTypes.STRING_NODES);
        }


    }
}
