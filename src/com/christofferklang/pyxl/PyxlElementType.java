package com.christofferklang.pyxl;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class PyxlElementType extends IElementType {
    public PyxlElementType(@NotNull @NonNls String debugName) {
        super(debugName, PyxlLanguage.INSTANCE);
    }
}
