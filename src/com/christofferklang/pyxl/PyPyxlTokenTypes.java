package com.christofferklang.pyxl;

import com.jetbrains.python.psi.PyElementType;

public class PyPyxlTokenTypes {
    private PyPyxlTokenTypes() {
        // Prevent instantiation
    }

    /** Dummy token to test out custom tokenization */
    public static final PyElementType BANANA_DUMMY = new PyElementType("BANANA DUMMY");
}
