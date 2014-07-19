package com.christofferklang.pyxl.parsing;

import com.intellij.lang.PsiBuilder;
import com.jetbrains.python.parsing.ExpressionParsing;
import com.jetbrains.python.parsing.ParsingContext;
import com.jetbrains.python.parsing.StatementParsing;
import com.jetbrains.python.psi.LanguageLevel;

class PyxlParsingContext extends ParsingContext {
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
