package com.christofferklang.pyxl.psi;

import com.christofferklang.pyxl.PyxlElementTypes;
import com.intellij.lang.ASTNode;
import com.jetbrains.python.psi.PyArgumentList;
import com.jetbrains.python.psi.PyExpression;
import com.jetbrains.python.psi.impl.PyCallExpressionImpl;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a PyxlTag as a Python call expression of a class.
 * The arguments of the Pyxl tag are interpreted as keyword arguments to the
 * __init__ method, while the tag body is interpreted as arguments to the class's
 * __call__ method.
 *
 * <tag my="value"><span>{"child" + "content"}</span></tag>
 * x_tag(my="value").__call__(
 *   x_span().__call__(
 *      ("child" + "content")
 *   )
 * )
 */
public class PyxlTag extends PyCallExpressionImpl {
    public PyxlTag(ASTNode astNode) {
        super(astNode);
    }

    public String getPythonClassName() {
        final PyExpression callee = getCallee();
        if(callee != null) {
            String name = callee.getName();
            if(name == null) {
                name = callee.getNode().getText();
            }
            return name;
        }
        return null;
    }

    @Override
    public String toString() {
        final String name = getPythonClassName();
        return String.format("Pyxl Tag: %s", name == null ? "null" : name);
    }

    @Nullable
    @Override
    public PyExpression getCallee() {
        return findChildByClass(PyExpression.class);
    }

    @Override
    public PyArgumentList getArgumentList() {
        return (PyArgumentList) findChildByType(PyxlElementTypes.ARGUMENT_LIST);
    }
}
