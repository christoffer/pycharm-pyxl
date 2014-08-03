package com.christofferklang.pyxl;

import com.jetbrains.python.psi.PyElementType;

public class PyxlTokenTypes {
    private PyxlTokenTypes() {
        // Prevent instantiation
    }

    public static final PyElementType BADCHAR = new PyElementType("PYXL BAD CHAR");
    public static final PyElementType ATTRNAME = new PyElementType("PYXL ATTRNAME");
    public static final PyElementType ATTRVALUE = new PyElementType("PYXL ATTRVALUE");
    public static final PyElementType ATTRVALUE_START = new PyElementType("PYXL ATTRVALUE BEGIN");
    public static final PyElementType ATTRVALUE_END = new PyElementType("PYXL ATTRVALUE END");

    public static final PyElementType TAGBEGIN = new PyElementType("PYXL TAGBEGIN <");
    public static final PyElementType TAGNAME_MODULE = new PyElementType("PYXL TAGNAME_MODULE");
    public static final PyElementType TAGNAME = new PyElementType("PYXL TAGNAME");
    public static final PyElementType TAGEND = new PyElementType("PYXL TAGEND >");
    public static final PyElementType TAGCLOSE = new PyElementType("PYXL TAGCLOSE </");

    public static final PyElementType TAGENDANDCLOSE = new PyElementType("PYXL TAGENDANDCLOSE />");

    public static final PyElementType BUILT_IN_TAG = new PyElementType("PYXL BUILT IN TAG");

    public static final PyElementType EMBED_START = new PyElementType("PYXL PYTHON EMBED BEGIN {");
    public static final PyElementType EMBED_END = new PyElementType("PYXL PYTHON EMBED END }");
    public static final PyElementType STRING = new PyElementType("PYXL STRING");
}
