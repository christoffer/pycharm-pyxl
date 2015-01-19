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
 * Represents <tag attr='val'> as the Python call x_tag(attr='val')
 */
public class PyxlClassInitCallExpression extends PyCallExpressionImpl {
    public PyxlClassInitCallExpression(ASTNode astNode) {
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
        PyClassReference pyClassRef = findChildByClass(PyClassReference.class);
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
        return findChildByClass(PyClassReference.class);
    }

    @Override
    public String toString() {
        final String name = getPythonClassName();
        final String args = getArgumentList() == null ? "<no args>" : getArgumentList().toString();
        return String.format("Pyxl class instantiation call: %s(%s)", (name == null ? "null" : name), args);
    }

    @Override
    public PyArgumentList getArgumentList() {
        return (PyArgumentList) findChildByType(PyxlElementTypes.ARGUMENT_LIST);
    }
}
