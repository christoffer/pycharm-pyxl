package com.christofferklang.pyxl.psi;

import com.christofferklang.pyxl.PyxlElementTypes;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.jetbrains.python.psi.PyArgumentList;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyExpression;
import com.jetbrains.python.psi.impl.PyCallExpressionImpl;
import org.jetbrains.annotations.Nullable;

/**
 * Represents <parent>string</parent> as the Python call: x_parent().__call__("string")
 */
public class PyxlInstanceCallExpression extends PyCallExpressionImpl {
    public PyxlInstanceCallExpression(ASTNode astNode) {
        super(astNode);
    }

    @Override
    public String toString() {
        return String.format("Pyxl instance call: %s(%s)", getCallee(), getArgumentList());
    }

    @Override
    public PyArgumentList getArgumentList() {
        return (PyArgumentList) findChildByType(PyxlElementTypes.ARGUMENT_LIST);
    }
}
