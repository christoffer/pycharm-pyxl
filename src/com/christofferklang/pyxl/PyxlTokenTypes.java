package com.christofferklang.pyxl;

import com.jetbrains.python.psi.PyElementType;

public class PyxlTokenTypes {
    private PyxlTokenTypes() {
        // Prevent instantiation
    }

    public static final PyElementType BADCHAR = new PyElementType("PYXL BAD CHAR");
    public static final PyElementType ATTRNAME = new PyElementType("PYXL ATTRNAME");
    public static final PyElementType ATTRVALUE = new PyElementType("PYXL ATTRVALUE");
    public static final PyElementType TAGEND = new PyElementType("PYXL TAG END");
    public static final PyElementType TAGCLOSE = new PyElementType("PYXL TAG CLOSE");

    public static final PyElementType TAGENDANDCLOSE = new PyElementType("PYXL TAG END AND CLOSE");
    public static final PyElementType TAGBEGIN = new PyElementType("PYXL TAG BEGIN");
    public static final PyElementType EMBED_START = new PyElementType("PYXL PYTHON EMBED BEGIN");
    public static final PyElementType EMBED_END = new PyElementType("PYXL PYTHON EMBED END");
    public static final PyElementType STRING = new PyElementType("PYXL STRING");

}
