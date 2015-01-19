package com.christofferklang.pyxl.psi;

import com.intellij.lang.ASTNode;
import com.jetbrains.python.psi.PyExpression;
import com.jetbrains.python.psi.impl.PyConditionalExpressionImpl;

/**
 * Pyxl <if> and <else>
 */
public class PyxlConditionalTag extends PyConditionalExpressionImpl {
    public PyxlConditionalTag(ASTNode astNode) {
        super(astNode);
    }

    @Override
    public PyExpression getTruePart() {
        return null;
    }

    @Override
    public PyExpression getCondition() {
        return null;
    }

    @Override
    public PyExpression getFalsePart() {
        return null;
    }
}
