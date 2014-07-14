package com.christofferklang.pyxl;

import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class PyxlFileTypeFactory extends FileTypeFactory {
    @Override
    public void createFileTypes(@NonNls @NotNull FileTypeConsumer consumer) {
        consumer.consume(PyxlFileType.INSTANCE, "pyxl");
    }
}
