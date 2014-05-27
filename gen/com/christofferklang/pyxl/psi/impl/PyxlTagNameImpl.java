// This is a generated file. Not intended for manual editing.
package com.christofferklang.pyxl.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.christofferklang.pyxl.psi.PyxlTypes.*;
import com.christofferklang.pyxl.psi.*;

public class PyxlTagNameImpl extends PyxlNamedElementImpl implements PyxlTagName {

  public PyxlTagNameImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof PyxlVisitor) ((PyxlVisitor)visitor).visitTagName(this);
    else super.accept(visitor);
  }

  public String getTag() {
    return PyxlPsiImplUtil.getTag(this);
  }

  public PsiElement getNameIdentifier() {
    return PyxlPsiImplUtil.getNameIdentifier(this);
  }

}
