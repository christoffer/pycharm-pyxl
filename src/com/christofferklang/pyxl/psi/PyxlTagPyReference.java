package com.christofferklang.pyxl.psi;

import com.intellij.lang.ASTNode;
import com.jetbrains.python.psi.impl.PyReferenceExpressionImpl;
import org.jetbrains.annotations.Nullable;

public class PyxlTagPyReference extends PyReferenceExpressionImpl {
    public PyxlTagPyReference(ASTNode astNode) {
        super(astNode);
    }

    @Nullable
    @Override
    public String getName() {
        return pyxlClassName(getText());
    }

    @Nullable
    @Override
    public String getReferencedName() {
        return getName();
    }

    private String pyxlClassName(String tagName) {
        return tagName.replaceFirst("</?", "x_").replaceFirst(">$", "");
    }
}
