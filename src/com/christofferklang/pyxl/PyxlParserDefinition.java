package com.christofferklang.pyxl;

import com.christofferklang.pyxl.psi.PyxlTypes;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;

import java.io.Reader;

public class PyxlParserDefinition implements ParserDefinition {
    public static final TokenSet WHITE_SPACES = TokenSet.create(PyxlTypes.WHITE_SPACE);
    public static final TokenSet COMMENTS = TokenSet.create(PyxlTypes.COMMENT);
    public static final TokenSet STRING_LITERALS = TokenSet.create(PyxlTypes.QUOTED_VALUE);

    private static final IFileElementType PYXL_FILE =
            new IFileElementType(Language.findInstance(PyxlLanguage.class));

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new FlexAdapter(new PyxlLexer((Reader) null));
    }

    @Override
    public PsiParser createParser(Project project) {
        return new PyxlParser();
    }

    @Override
    public IFileElementType getFileNodeType() {
        return PYXL_FILE;
    }

    @NotNull
    @Override
    public TokenSet getWhitespaceTokens() {
        return WHITE_SPACES;
    }

    @NotNull
    @Override
    public TokenSet getCommentTokens() {
        return COMMENTS;
    }

    @NotNull
    @Override
    public TokenSet getStringLiteralElements() {
        return STRING_LITERALS;
    }

    @NotNull
    @Override
    public PsiElement createElement(ASTNode astNode) {
        return PyxlTypes.Factory.createElement(astNode);
    }

    @Override
    public PsiFile createFile(FileViewProvider fileViewProvider) {
        return new PyxlPsiFile(fileViewProvider);
    }

    @Override
    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }
}
