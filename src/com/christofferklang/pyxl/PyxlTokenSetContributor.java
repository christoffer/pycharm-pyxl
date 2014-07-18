package com.christofferklang.pyxl;

import com.intellij.psi.tree.TokenSet;
import com.jetbrains.python.PythonTokenSetContributor;
import org.jetbrains.annotations.NotNull;

public class PyxlTokenSetContributor extends PythonTokenSetContributor {
    @NotNull
    @Override
    public TokenSet getExpressionTokens() {
        return TokenSet.orSet(super.getExpressionTokens(), TokenSet.create(PyxlElementTypes.TAG_REFERENCE));
    }
}
