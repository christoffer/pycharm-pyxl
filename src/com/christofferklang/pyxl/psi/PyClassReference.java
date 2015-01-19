package com.christofferklang.pyxl.psi;

import com.intellij.lang.ASTNode;
import com.jetbrains.python.psi.*;
import com.jetbrains.python.psi.impl.PyReferenceExpressionImpl;
import org.jetbrains.annotations.Nullable;

/**
 * A simple reference to a PyClass object
 */
public class PyClassReference extends PyReferenceExpressionImpl {
    public PyClassReference(ASTNode astNode) {
        super(astNode);
    }

    @Nullable
    @Override
    public String getReferencedName() {
        return Helpers.pyxlToPythonClass(getNode().getText());
    }

    @Nullable
    @Override
    public PyExpression getQualifier() {
        PyExpression realQualifier = super.getQualifier();
        if (realQualifier == null && Helpers.PYXL_TAG_NAMES.contains(getReferencedName())) {
            // Implicitly assume the tag is a reference to a pyxl html tag if the pyxl html module is imported and we
            // aren't using a qualifier already. This will break resolution of tags defined in a more local scope than
            // pyxl.html (e.g. if you make your own class x_div in a file that also imports pyxl.html).
            // This is consistent with how Pyxl works:
            // https://github.com/dropbox/pyxl/blob/daa01ca026ef3dba931d3ba56118ad8f8f6bec94/pyxl/codec/parser.py#L211
            if (getContainingFile() instanceof PyFile) {
                final PyFile pyFile = (PyFile) getContainingFile();
                PyImportElement pyxlHtmlImportElement = Helpers.getImportedPyxlHtmlModuleElementFromFile(pyFile);
                if (pyxlHtmlImportElement != null) {
                    return pyxlHtmlImportElement.getImportReferenceExpression();
                }
            }
        }
        return realQualifier;
    }

    @Override
    public String toString() {
        return "PyClassTagReference: " + getReferencedName();
    }
}
