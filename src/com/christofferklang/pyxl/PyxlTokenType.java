package com.christofferklang.pyxl;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class PyxlTokenType extends IElementType {
    public PyxlTokenType(@NotNull @NonNls String debugName) {
        super(debugName, PyxlLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "PyxlTokenType." + super.toString();
    }
}
