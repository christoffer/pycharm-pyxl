package com.christofferklang.pyxl.psi;

import com.christofferklang.pyxl.PyxlElementTypes;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.jetbrains.python.psi.PyArgumentList;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyExpression;
import com.jetbrains.python.psi.impl.PyCallExpressionImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a PyxlTag as a Python call expression of a class.
 * The arguments of the Pyxl tag are interpreted as keyword arguments to the
 * __init__ method, while the tag body is interpreted as arguments to the class's
 * __call__ method.
 * <p/>
 * <tag my="value"><span>{"child" + "content"}</span></tag>
 * x_tag(my="value").__call__(
 * x_span().__call__(
 * ("child" + "content")
 * )
 * )
 */
public class PyxlTag extends PyCallExpressionImpl {
    public PyxlTag(ASTNode astNode) {
        super(astNode);
    }

    /**
     * Returns the name of the referenced class in Python, or null.
     */
    public String getPythonClassName() {
        PyClass referencedPyClass = getReferencedPythonClass();
        if(referencedPyClass != null) {
            return referencedPyClass.getQualifiedName();
        }
        return null;
    }

    /**
     * Dereferences the PyClass from the Pyxl tag.
     */
    public PyClass getReferencedPythonClass() {
        PythonClassReference pyClassRef = findChildByClass(PythonClassReference.class);
        if(pyClassRef != null) {
            final PsiElement resolved = pyClassRef.getReference().resolve();
            if(resolved instanceof PyClass) {
                return (PyClass) resolved;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public PyExpression getCallee() {
        return findChildByClass(PythonClassReference.class);
    }

    @Override
    public String toString() {
        final String name = getPythonClassName();
        return String.format("Pyxl Tag: %s", name == null ? "null" : name);
    }

    @Override
    public PyArgumentList getArgumentList() {
        return (PyArgumentList) findChildByType(PyxlElementTypes.ARGUMENT_LIST);
    }
}
