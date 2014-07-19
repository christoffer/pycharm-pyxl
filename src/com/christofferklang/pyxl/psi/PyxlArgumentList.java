package com.christofferklang.pyxl.psi;

import com.intellij.lang.ASTNode;
import com.jetbrains.python.psi.impl.PyArgumentListImpl;

public class PyxlArgumentList extends PyArgumentListImpl {
    public PyxlArgumentList(ASTNode astNode) {
        super(astNode);
    }
}
