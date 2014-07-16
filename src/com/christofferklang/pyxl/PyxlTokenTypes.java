package com.christofferklang.pyxl;

import com.jetbrains.python.psi.PyElementType;

public class PyxlTokenTypes {
    private PyxlTokenTypes() {
        // Prevent instantiation
    }

    /** Dummy token to test out custom tokenization */
    public static final PyElementType BANANA_DUMMY = new PyElementType("BANANA DUMMY");

    public static final PyElementType FRAGBEGIN = new PyElementType("PYXL FRAG BEGIN");
    public static final PyElementType BADCHAR = new PyElementType("PYXL BAD CHAR");
    public static final PyElementType FRAGEND = new PyElementType("PYXL FRAG END");
    public static final PyElementType IDENTIFIER = new PyElementType("PYXL IDENTIFIER");
    public static final PyElementType TAGEND = new PyElementType("PYXL TAG END");
    public static final PyElementType TAGCLOSE = new PyElementType("PYXL TAG CLOSE");

    public static final PyElementType TAGENDANDCLOSE = new PyElementType("PYXL TAG END AND CLOSE");
    public static final PyElementType TAGBEGIN = new PyElementType("PYXL TAG BEGIN");
    public static final PyElementType EMBED_START = new PyElementType("PYXL PYTHON EMBED BEGIN");
    public static final PyElementType EMBED_END = new PyElementType("PYXL PYTHON EMBED END");
}
