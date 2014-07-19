package com.christofferklang.pyxl.parsing;

import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.tree.TokenSet;
import com.jetbrains.python.PythonParserDefinition;
import com.jetbrains.python.lexer.PythonIndentingProcessor;
import org.jetbrains.annotations.NotNull;

import java.io.Reader;

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

    private class PyxlIndentingLexer extends PythonIndentingProcessor {
        public PyxlIndentingLexer() {
            super(new PyxlLexer((Reader) null), TokenSet.EMPTY);
        }
    }
}
