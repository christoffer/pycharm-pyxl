package com.christofferklang.pyxl.psi;

import com.intellij.lang.ASTNode;
import com.jetbrains.python.psi.impl.PyStringLiteralExpressionImpl;

public class PyxlAttrName extends PyStringLiteralExpressionImpl {
    public PyxlAttrName(ASTNode astNode) {
        super(astNode);
    }


}
