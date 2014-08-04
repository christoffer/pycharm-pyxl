package com.christofferklang.pyxl;

import com.christofferklang.pyxl.psi.PyxlTag;
import com.intellij.psi.tree.TokenSet;
import com.jetbrains.python.PythonDialectsTokenSetContributor;
import org.jetbrains.annotations.NotNull;

public class PyxlDialectTokenContributor implements PythonDialectsTokenSetContributor {
    @NotNull
    @Override
    public TokenSet getStatementTokens() {
        return TokenSet.EMPTY;
    }

    @NotNull
    @Override
    public TokenSet getExpressionTokens() {
        return TokenSet.create(PyxlElementTypes.TAG, PyxlElementTypes.MODULE_REFERENCE);
    }

    @NotNull
    @Override
    public TokenSet getNameDefinerTokens() {
        return TokenSet.EMPTY;
    }

    @NotNull
    @Override
    public TokenSet getKeywordTokens() {
        return TokenSet.EMPTY;
    }

    @NotNull
    @Override
    public TokenSet getParameterTokens() {
        return TokenSet.EMPTY;
    }

    @NotNull
    @Override
    public TokenSet getFunctionDeclarationTokens() {
        return TokenSet.EMPTY;
    }

    @NotNull
    @Override
    public TokenSet getUnbalancedBracesRecoveryTokens() {
        return TokenSet.EMPTY;
    }

    @NotNull
    @Override
    public TokenSet getReferenceExpressionTokens() {
        return TokenSet.EMPTY;
    }
}
