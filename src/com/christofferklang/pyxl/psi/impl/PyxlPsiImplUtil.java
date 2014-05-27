package com.christofferklang.pyxl.psi.impl;

import com.christofferklang.pyxl.psi.PyxlTag;
import com.christofferklang.pyxl.psi.PyxlTagName;
import com.christofferklang.pyxl.psi.PyxlTypes;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;

public class PyxlPsiImplUtil {
    public static String getTag(PyxlTagName element) {
        ASTNode keyNode = element.getNode().findChildByType(PyxlTypes.TAG);
        if(keyNode != null) {
            return keyNode.getText();
        } else {
            return null;
        }
    }

    public static PsiElement getNameIdentifier(PyxlTagName pyxlTag) {
        ASTNode keyNode = pyxlTag.getNode().findChildByType(PyxlTypes.TAG);
        return keyNode != null ? keyNode.getPsi() : null;
    }
}
