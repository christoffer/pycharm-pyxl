package com.christofferklang.pyxl.parsing;

import com.christofferklang.pyxl.PyxlTokenTypes;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.tree.TokenSet;
import com.jetbrains.python.PythonParserDefinition;
import org.jetbrains.annotations.NotNull;

public class PyxlParserDefinition extends PythonParserDefinition {

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new PyxlIndentingLexer();
    }

    @NotNull
    public PsiParser createParser(Project project) {
        return new PyxlParser();
    }

    @NotNull
    @Override
    public TokenSet getStringLiteralElements() {
        return TokenSet.orSet(super.getStringLiteralElements(), TokenSet.create(PyxlTokenTypes.STRING));
    }
}
