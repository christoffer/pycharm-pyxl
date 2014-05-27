// This is a generated file. Not intended for manual editing.
package com.christofferklang.pyxl.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.christofferklang.pyxl.psi.PyxlTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.christofferklang.pyxl.psi.*;

public class PyxlContentImpl extends ASTWrapperPsiElement implements PyxlContent {

  public PyxlContentImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof PyxlVisitor) ((PyxlVisitor)visitor).visitContent(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<PyxlTag> getTagList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, PyxlTag.class);
  }

}
