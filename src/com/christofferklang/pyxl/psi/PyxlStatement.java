package com.christofferklang.pyxl.psi;

import com.intellij.lang.ASTNode;
import com.jetbrains.python.psi.impl.PyStringLiteralExpressionImpl;

public class PyxlStatement extends PyStringLiteralExpressionImpl {
    public PyxlStatement(ASTNode astNode) {
        super(astNode);
    }
}
