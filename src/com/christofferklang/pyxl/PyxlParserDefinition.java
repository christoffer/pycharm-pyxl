package com.christofferklang.pyxl;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.jetbrains.python.PyElementTypes;
import com.jetbrains.python.PyTokenTypes;
import com.jetbrains.python.PythonParserDefinition;
import com.jetbrains.python.lexer.PythonIndentingProcessor;
import com.jetbrains.python.parsing.*;
import com.jetbrains.python.psi.LanguageLevel;
import org.jetbrains.annotations.NotNull;

import java.io.Reader;

public class PyxlParserDefinition extends PythonParserDefinition {

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new PyxlIndentingLexer();
    }

    private class PyxlIndentingLexer extends PythonIndentingProcessor {
        public PyxlIndentingLexer() {
            super(new PyxlLexer((Reader) null), TokenSet.EMPTY);
        }
    }


    @NotNull
    public PsiParser createParser(Project project) {
        return new PyxlParser();
    }

    private class PyxlParser extends PyParser {
        protected ParsingContext createParsingContext(
                PsiBuilder builder, LanguageLevel languageLevel,
                StatementParsing.FUTURE futureFlag) {

            builder.setDebugMode(true);
            return new PyxlParsingContext(builder, languageLevel, futureFlag);
        }
    }

    private class PyxlParsingContext extends ParsingContext {
        private final PyxlExpressionParsing pyxlExpressionParser;

        public PyxlParsingContext(
                final PsiBuilder builder, LanguageLevel languageLevel,
                StatementParsing.FUTURE futureFlag) {
            super(builder, languageLevel, futureFlag);
            pyxlExpressionParser = new PyxlExpressionParsing(this);
        }

        public ExpressionParsing getExpressionParser() {
            return pyxlExpressionParser;
        }
    }

    private class PyxlExpressionParsing extends ExpressionParsing {
        public PyxlExpressionParsing(ParsingContext context) {
            super(context);
        }

        public boolean parsePrimaryExpression(boolean isTargetExpression) {
            boolean match = super.parsePrimaryExpression(isTargetExpression);
            if (!match) {
                final IElementType firstToken = myBuilder.getTokenType();
                if (firstToken == PyTokenTypes.LT) {
                    parsePyxlTagBegin();
                    return true;
                }
            }
            return match;
        }



      private void parsePyxlAttributes() {
          myBuilder.advanceLexer();
          if (myBuilder.getTokenType() == PyTokenTypes.IDENTIFIER) {
              myBuilder.advanceLexer();
              if (myBuilder.getTokenType() == PyTokenTypes.EQ) {
                  myBuilder.advanceLexer();
                  if (myBuilder.getTokenType() == PyTokenTypes.SINGLE_QUOTED_STRING) {
                      parsePyxlAttributes();
                      return;
                  }
              }

              myBuilder.error("pyxl attr expected");
          }
      }

      private void parsePyxlTagBegin() {
          final PsiBuilder.Marker expr = myBuilder.mark();
          myBuilder.advanceLexer();
          if (myBuilder.getTokenType() == PyTokenTypes.IDENTIFIER) {
              parsePyxlAttributes();

              if (myBuilder.getTokenType() == PyTokenTypes.GT) {
                  myBuilder.advanceLexer();

                  if (myBuilder.getTokenType() == PyTokenTypes.LT) {
                      if (myBuilder.lookAhead(1) != PyTokenTypes.DIV) {
                          parsePyxlTagBegin();
                      }
                      myBuilder.advanceLexer();
                      myBuilder.advanceLexer();

                      if (myBuilder.getTokenType() == PyTokenTypes.IDENTIFIER) {
                          myBuilder.advanceLexer();
                          if (myBuilder.getTokenType() == PyTokenTypes.GT) {
                              myBuilder.advanceLexer();
                              expr.done(PyElementTypes.STRING_LITERAL_EXPRESSION);
                              return;
                          }
                      }
                  }
              } else if (myBuilder.getTokenType() == PyTokenTypes.DIV) {
                  myBuilder.advanceLexer();
                  if (myBuilder.getTokenType() == PyTokenTypes.GT) {
                      myBuilder.advanceLexer();
                      expr.done(PyElementTypes.STRING_LITERAL_EXPRESSION);
                      return;
                  }
              }
          }

          myBuilder.error("pyxl expected");
          expr.done(PyElementTypes.STRING_LITERAL_EXPRESSION);
      }
    }
}
