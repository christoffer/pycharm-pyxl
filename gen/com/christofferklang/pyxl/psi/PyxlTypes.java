// This is a generated file. Not intended for manual editing.
package com.christofferklang.pyxl.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.christofferklang.pyxl.PyxlElementType;
import com.christofferklang.pyxl.PyxlTokenType;
import com.christofferklang.pyxl.psi.impl.*;

public interface PyxlTypes {

  IElementType CONTENT = new PyxlElementType("CONTENT");
  IElementType TAG = new PyxlElementType("TAG");
  IElementType TAG_NAME = new PyxlElementType("TAG_NAME");

  IElementType ATTR_NAME = new PyxlTokenType("ATTR_NAME");
  IElementType COMMENT = new PyxlTokenType("COMMENT");
  IElementType EQ = new PyxlTokenType("EQ");
  IElementType IDENTIFIER = new PyxlTokenType("IDENTIFIER");
  IElementType LEFT_ANGLE = new PyxlTokenType("LEFT_ANGLE");
  IElementType PYTHON_TEXT = new PyxlTokenType("PYTHON_TEXT");
  IElementType QUOTED_VALUE = new PyxlTokenType("QUOTED_VALUE");
  IElementType RIGHT_ANGLE = new PyxlTokenType("RIGHT_ANGLE");
  IElementType SELF_CLOSE_END = new PyxlTokenType("SELF_CLOSE_END");
  IElementType START_OF_CLOSE_TAG = new PyxlTokenType("START_OF_CLOSE_TAG");
  IElementType TEXT = new PyxlTokenType("TEXT");
  IElementType UNEXPECTED_CLOSING_TAG = new PyxlTokenType("UNEXPECTED_CLOSING_TAG");
  IElementType WHITE_SPACE = new PyxlTokenType("WHITE_SPACE");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
       if (type == CONTENT) {
        return new PyxlContentImpl(node);
      }
      else if (type == TAG) {
        return new PyxlTagImpl(node);
      }
      else if (type == TAG_NAME) {
        return new PyxlTagNameImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
