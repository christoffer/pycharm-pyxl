package com.christofferklang.pyxl.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.QualifiedName;
import com.jetbrains.python.psi.*;
import com.jetbrains.python.psi.impl.PyReferenceExpressionImpl;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PyTagClassReference extends PyReferenceExpressionImpl {
    private static final Set<String> EMPTY_HASH_SET = new HashSet<String>();

    private Set<String> mCachedSpecialPyxlTagNames = null;

    public PyTagClassReference(ASTNode astNode) {
        super(astNode);
    }

    @Nullable
    @Override
    public String getReferencedName() {
        return pyxlClassName(getNode().getText());
    }

    @Nullable
    @Override
    public PyExpression getQualifier() {
        PyExpression realQualifier = super.getQualifier();
        if (realQualifier == null && isPyxlHtmlTag(getReferencedName())) {
            // Implicitly assume the tag is a reference to a pyxl html tag if the pyxl html module is imported and we
            // aren't using a qualifier already. This will break resolution of tags defined in a more local scope than
            // pyxl.html (e.g. if you make your own class x_div in a file that also imports pyxl.html).
            // This is consistent with how Pyxl works:
            // https://github.com/dropbox/pyxl/blob/daa01ca026ef3dba931d3ba56118ad8f8f6bec94/pyxl/codec/parser.py#L211
            PyImportElement pyxlHtmlImportElement = getImportedPyxlHtmlModuleElement();
            if (pyxlHtmlImportElement != null) {
                return pyxlHtmlImportElement.getImportReferenceExpression();
            }
        }
        return realQualifier;
    }

    private boolean isPyxlHtmlTag(String name) {
        return getSpecialPyxlTagsFromImportedHtmlModule().contains(name);
    }

    private Set<String> getSpecialPyxlTagsFromImportedHtmlModule() {
        if (mCachedSpecialPyxlTagNames == null) {
            PyImportElement importPyxlHtmlElement = getImportedPyxlHtmlModuleElement();
            if (importPyxlHtmlElement != null) {
                PyFile htmlModule = (PyFile) importPyxlHtmlElement.getElementNamed("html");

                mCachedSpecialPyxlTagNames = new HashSet<String>();
                //noinspection ConstantConditions
                for (PyClass topLevelClass : htmlModule.getTopLevelClasses()) {
                    mCachedSpecialPyxlTagNames.add(topLevelClass.getName());
                }
            }
        }

        return mCachedSpecialPyxlTagNames == null ? EMPTY_HASH_SET : mCachedSpecialPyxlTagNames;
    }

    private PyImportElement getImportedPyxlHtmlModuleElement() {
        if (!(getContainingFile() instanceof PyFile)) return null; // not a python file

        List<PyFromImportStatement> imports = ((PyFile) getContainingFile()).getFromImports();

        for (PyFromImportStatement importStatement : imports) {
            QualifiedName qualifiedImportName = importStatement.getImportSourceQName();
            // check for import statements that import from a "pyxl" package
            if (hasLastComponent("pyxl", importStatement.getImportSourceQName())) {
                // check only for imports of the module "html"
                PyImportElement[] importedElements = importStatement.getImportElements();
                for (PyImportElement importedElement : importedElements) {
                    PsiElement htmlElement = importedElement.getElementNamed("html");
                    if (htmlElement instanceof PyFile) {
                        return importedElement;
                    }
                }
            }
        }

        return null;
    }

    private boolean hasLastComponent(String componentName, QualifiedName qualifiedName) {
        return qualifiedName != null
                && qualifiedName.getLastComponent() != null
                && qualifiedName.getLastComponent().equals(componentName);
    }

    private String pyxlClassName(String tagName) {
        return "x_" + tagName;
    }

    @Override
    public String toString() {
        return "PyClassTagReference: " + getReferencedName();
    }
}
