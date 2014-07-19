package com.christofferklang.pyxl.parsing;

import com.intellij.lang.PsiBuilder;
import com.jetbrains.python.parsing.ParsingContext;
import com.jetbrains.python.parsing.PyParser;
import com.jetbrains.python.parsing.StatementParsing;
import com.jetbrains.python.psi.LanguageLevel;

class PyxlParser extends PyParser {
    protected ParsingContext createParsingContext(
            PsiBuilder builder, LanguageLevel languageLevel,
            StatementParsing.FUTURE futureFlag) {

        builder.setDebugMode(true);
        return new PyxlParsingContext(builder, languageLevel, futureFlag);
    }
}
