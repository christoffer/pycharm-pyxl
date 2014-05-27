package com.christofferklang.pyxl;

import com.christofferklang.pyxl.psi.PyxlTypes;
import com.intellij.lang.Language;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.ex.util.LayerDescriptor;
import com.intellij.openapi.editor.ex.util.LayeredLexerEditorHighlighter;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.templateLanguages.TemplateDataLanguageMappings;
import com.jetbrains.python.PythonFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PyxlTemplateHighlighter extends LayeredLexerEditorHighlighter {
    public PyxlTemplateHighlighter(@Nullable Project project, VirtualFile virtualFile, @NotNull EditorColorsScheme editorColorsScheme) {
        super(new PyxlHighlighter(), editorColorsScheme);

        // highlighter for outer lang
        FileType type = null;
        if (project == null || virtualFile == null) {
            type = StdFileTypes.PLAIN_TEXT;
        } else {
            Language language = TemplateDataLanguageMappings.getInstance(project).getMapping(virtualFile);
            if (language != null) type = language.getAssociatedFileType();
            if (type == null) type = PythonFileType.INSTANCE; /* default */
        }

        @SuppressWarnings("deprecation")
        SyntaxHighlighter outerHighlighter = SyntaxHighlighter.PROVIDER.create(type, project, virtualFile);

        registerLayer(PyxlTypes.PYTHON_TEXT, new LayerDescriptor(outerHighlighter, ""));
    }
}