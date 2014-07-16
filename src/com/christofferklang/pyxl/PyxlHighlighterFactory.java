package com.christofferklang.pyxl;

import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.python.highlighting.PySyntaxHighlighterFactory;
import com.jetbrains.python.psi.LanguageLevel;
import com.jetbrains.python.psi.PyFile;
import org.jetbrains.annotations.NotNull;

public class PyxlHighlighterFactory extends PySyntaxHighlighterFactory {
    @NotNull
    @Override
    public SyntaxHighlighter getSyntaxHighlighter(Project project, VirtualFile virtualFile) {
        return new PyxlHighlighter(getLanguageLevelForFile(virtualFile));
    }

    private LanguageLevel getLanguageLevelForFile(VirtualFile virtualFile) {
        if (virtualFile instanceof PyFile) {
            return ((PyFile) virtualFile).getLanguageLevel();
        }
        return LanguageLevel.getDefault();
    }
}
