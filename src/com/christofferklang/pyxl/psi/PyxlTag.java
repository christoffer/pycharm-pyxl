package com.christofferklang.pyxl.psi;

import com.intellij.lang.ASTNode;
import com.jetbrains.python.psi.impl.PyStringLiteralExpressionImpl;

public class PyxlTag extends PyStringLiteralExpressionImpl {
    public PyxlTag(ASTNode astNode) {
        super(astNode);
    }

    @Override
    public String toString() {
        return "Pyxl Tag: " + getStringValue();
    }
}
