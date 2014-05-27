package com.christofferklang.pyxl;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeRegistry;
import com.intellij.openapi.util.io.ByteSequence;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.python.PythonFileType;
import com.jetbrains.python.PythonLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PyxlFileTypeDetector implements FileTypeRegistry.FileTypeDetector {
    @Nullable
    @Override
    public FileType detect(@NotNull VirtualFile virtualFile, @NotNull ByteSequence byteSequence, @Nullable CharSequence charSequence) {
        return PythonFileType.INSTANCE;
    }
}
