package com.christofferklang.pyxl.psi;

import com.christofferklang.pyxl.PyxlElementTypes;
import com.intellij.lang.ASTNode;
import com.jetbrains.python.psi.PyArgumentList;
import com.jetbrains.python.psi.PyExpression;
import com.jetbrains.python.psi.impl.PyCallExpressionImpl;
import org.jetbrains.annotations.Nullable;

public class PyxlTag extends PyCallExpressionImpl {
    private String pyxlTagName;

    public PyxlTag(ASTNode astNode) {
        super(astNode);
    }

    @Override
    public String toString() {
        PyExpression callee = getCallee();
        return String.format("Pyxl Tag: %s", callee == null ? "null" : callee.getName());
    }

    @Nullable
    @Override
    public PyExpression getCallee() {
        return (PyExpression) findChildByType(PyxlElementTypes.TAG_REFERENCE);
    }

    @Override
    public PyArgumentList getArgumentList() {
        return (PyArgumentList) findChildByType(PyxlElementTypes.ARGUMENT_LIST);
    }
}
