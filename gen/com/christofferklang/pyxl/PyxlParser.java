// This is a generated file. Not intended for manual editing.
package com.christofferklang.pyxl;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.openapi.diagnostic.Logger;
import static com.christofferklang.pyxl.psi.PyxlTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class PyxlParser implements PsiParser {

  public static final Logger LOG_ = Logger.getInstance("com.christofferklang.pyxl.PyxlParser");

  public ASTNode parse(IElementType root_, PsiBuilder builder_) {
    boolean result_;
    builder_ = adapt_builder_(root_, builder_, this, null);
    Marker marker_ = enter_section_(builder_, 0, _COLLAPSE_, null);
    if (root_ == CONTENT) {
      result_ = Content(builder_, 0);
    }
    else if (root_ == TAG) {
      result_ = Tag(builder_, 0);
    }
    else if (root_ == TAG_NAME) {
      result_ = TagName(builder_, 0);
    }
    else {
      result_ = parse_root_(root_, builder_, 0);
    }
    exit_section_(builder_, 0, marker_, root_, result_, true, TRUE_CONDITION);
    return builder_.getTreeBuilt();
  }

  protected boolean parse_root_(final IElementType root_, final PsiBuilder builder_, final int level_) {
    return pyxlFile(builder_, level_ + 1);
  }

  /* ********************************************************** */
  // ATTR_NAME EQ QUOTED_VALUE
  static boolean Attribute(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Attribute")) return false;
    if (!nextTokenIs(builder_, ATTR_NAME)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, ATTR_NAME, EQ, QUOTED_VALUE);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // (WHITE_SPACE | Tag | TEXT)*
  public static boolean Content(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Content")) return false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<content>");
    int pos_ = current_position_(builder_);
    while (true) {
      if (!Content_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "Content", pos_)) break;
      pos_ = current_position_(builder_);
    }
    exit_section_(builder_, level_, marker_, CONTENT, true, false, null);
    return true;
  }

  // WHITE_SPACE | Tag | TEXT
  private static boolean Content_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Content_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, WHITE_SPACE);
    if (!result_) result_ = Tag(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, TEXT);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // COMMENT | (TagHead SELF_CLOSE_END) | (TagHead RIGHT_ANGLE Content TagTail)
  public static boolean Tag(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Tag")) return false;
    if (!nextTokenIs(builder_, "<tag>", COMMENT, LEFT_ANGLE)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, "<tag>");
    result_ = consumeToken(builder_, COMMENT);
    if (!result_) result_ = Tag_1(builder_, level_ + 1);
    if (!result_) result_ = Tag_2(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, TAG, result_, false, null);
    return result_;
  }

  // TagHead SELF_CLOSE_END
  private static boolean Tag_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Tag_1")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = TagHead(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, SELF_CLOSE_END);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // TagHead RIGHT_ANGLE Content TagTail
  private static boolean Tag_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "Tag_2")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = TagHead(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, RIGHT_ANGLE);
    result_ = result_ && Content(builder_, level_ + 1);
    result_ = result_ && TagTail(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // LEFT_ANGLE TagName (Attribute)*
  static boolean TagHead(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "TagHead")) return false;
    if (!nextTokenIs(builder_, LEFT_ANGLE)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, LEFT_ANGLE);
    result_ = result_ && TagName(builder_, level_ + 1);
    result_ = result_ && TagHead_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // (Attribute)*
  private static boolean TagHead_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "TagHead_2")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!TagHead_2_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "TagHead_2", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // (Attribute)
  private static boolean TagHead_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "TagHead_2_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = Attribute(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // IDENTIFIER
  public static boolean TagName(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "TagName")) return false;
    if (!nextTokenIs(builder_, IDENTIFIER)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, IDENTIFIER);
    exit_section_(builder_, marker_, TAG_NAME, result_);
    return result_;
  }

  /* ********************************************************** */
  // START_OF_CLOSE_TAG TagName RIGHT_ANGLE
  static boolean TagTail(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "TagTail")) return false;
    if (!nextTokenIs(builder_, START_OF_CLOSE_TAG)) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, START_OF_CLOSE_TAG);
    result_ = result_ && TagName(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, RIGHT_ANGLE);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // (PYTHON_TEXT* Tag PYTHON_TEXT*)+ | PYTHON_TEXT*
  static boolean pyxlFile(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "pyxlFile")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = pyxlFile_0(builder_, level_ + 1);
    if (!result_) result_ = pyxlFile_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // (PYTHON_TEXT* Tag PYTHON_TEXT*)+
  private static boolean pyxlFile_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "pyxlFile_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = pyxlFile_0_0(builder_, level_ + 1);
    int pos_ = current_position_(builder_);
    while (result_) {
      if (!pyxlFile_0_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "pyxlFile_0", pos_)) break;
      pos_ = current_position_(builder_);
    }
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // PYTHON_TEXT* Tag PYTHON_TEXT*
  private static boolean pyxlFile_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "pyxlFile_0_0")) return false;
    boolean result_ = false;
    Marker marker_ = enter_section_(builder_);
    result_ = pyxlFile_0_0_0(builder_, level_ + 1);
    result_ = result_ && Tag(builder_, level_ + 1);
    result_ = result_ && pyxlFile_0_0_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // PYTHON_TEXT*
  private static boolean pyxlFile_0_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "pyxlFile_0_0_0")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeToken(builder_, PYTHON_TEXT)) break;
      if (!empty_element_parsed_guard_(builder_, "pyxlFile_0_0_0", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // PYTHON_TEXT*
  private static boolean pyxlFile_0_0_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "pyxlFile_0_0_2")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeToken(builder_, PYTHON_TEXT)) break;
      if (!empty_element_parsed_guard_(builder_, "pyxlFile_0_0_2", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

  // PYTHON_TEXT*
  private static boolean pyxlFile_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "pyxlFile_1")) return false;
    int pos_ = current_position_(builder_);
    while (true) {
      if (!consumeToken(builder_, PYTHON_TEXT)) break;
      if (!empty_element_parsed_guard_(builder_, "pyxlFile_1", pos_)) break;
      pos_ = current_position_(builder_);
    }
    return true;
  }

}
