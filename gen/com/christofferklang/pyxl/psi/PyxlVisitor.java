// This is a generated file. Not intended for manual editing.
package com.christofferklang.pyxl.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;

public class PyxlVisitor extends PsiElementVisitor {

  public void visitContent(@NotNull PyxlContent o) {
    visitPsiElement(o);
  }

  public void visitTag(@NotNull PyxlTag o) {
    visitPsiElement(o);
  }

  public void visitTagName(@NotNull PyxlTagName o) {
    visitNamedElement(o);
  }

  public void visitNamedElement(@NotNull PyxlNamedElement o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
