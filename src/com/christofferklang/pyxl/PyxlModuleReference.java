package com.christofferklang.pyxl;

import com.intellij.lang.ASTNode;
import com.jetbrains.python.psi.impl.PyReferenceExpressionImpl;
import org.jetbrains.annotations.Nullable;

public class PyxlModuleReference extends PyReferenceExpressionImpl {
    public PyxlModuleReference(ASTNode astNode) {
        super(astNode);
    }

    @Nullable
    @Override
    public ASTNode getNameElement() {
        return getNode().findChildByType(PyxlTokenTypes.TAGNAME_MODULE);
    }
}
