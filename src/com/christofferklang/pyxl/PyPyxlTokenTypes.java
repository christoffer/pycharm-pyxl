package com.christofferklang.pyxl;

import com.jetbrains.python.psi.PyElementType;

public class PyPyxlTokenTypes {
    private PyPyxlTokenTypes() {
        // Prevent instantiation
    }

    /** Dummy token to test out custom tokenization */
    public static final PyElementType BANANA_DUMMY = new PyElementType("BANANA DUMMY");

    public static final PyElementType PYXL_FRAGBEGIN = new PyElementType("PYXL FRAG BEGIN");
    public static final PyElementType PYXL_BADCHAR = new PyElementType("PYXL BAD CHAR");
    public static final PyElementType PYXL_FRAGEND = new PyElementType("PYXL FRAG END");
    public static final PyElementType PYXL_IDENTIFIER = new PyElementType("PYXL IDENTIFIER");
    public static final PyElementType PYXL_TAGEND = new PyElementType("PYXL TAG END");
    public static final PyElementType PYXL_TAGCLOSE = new PyElementType("PYXL TAG CLOSE");

    public static final PyElementType PYXL_TAGENDANDCLOSE = new PyElementType("PYXL TAG END AND CLOSE");
    public static final PyElementType PYXL_TAGBEGIN = new PyElementType("PYXL TAG BEGIN");
    public static final PyElementType PYTHON_EMBED_START = new PyElementType("PYXL PYTHON EMBED BEGIN");
    public static final PyElementType PYTHON_EMBED_END = new PyElementType("PYXL PYTHON EMBED END");
}
