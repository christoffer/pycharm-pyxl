package com.christofferklang.pyxl;

import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.fileTypes.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.python.PythonFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class PyxlFileType extends LanguageFileType implements TemplateLanguageFileType {
    public static final PyxlFileType INSTANCE = new PyxlFileType();

    public PyxlFileType() {
        super(PyxlLanguage.INSTANCE);

        FileTypeEditorHighlighterProviders.INSTANCE.addExplicitExtension(this, new PyxlEditorHighlighter());
    }

    @NotNull
    @Override
    public String getName() {
        return "Pyxl file";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Pyxl file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "py";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return PythonFileType.INSTANCE.getIcon();
    }

    private static class PyxlEditorHighlighter implements EditorHighlighterProvider {
        @Override
        public EditorHighlighter getEditorHighlighter(@Nullable Project project, @NotNull FileType fileType, @Nullable VirtualFile virtualFile, @NotNull EditorColorsScheme editorColorsScheme) {
            return new PyxlTemplateHighlighter(project, virtualFile, editorColorsScheme);
        }
    }
}
