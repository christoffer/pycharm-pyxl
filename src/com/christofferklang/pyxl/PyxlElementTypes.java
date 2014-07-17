package com.christofferklang.pyxl;

import com.christofferklang.pyxl.psi.PyxlStatement;
import com.christofferklang.pyxl.psi.PyxlTagPyReference;
import com.intellij.psi.tree.IElementType;
import com.jetbrains.python.psi.PyElementType;

public class PyxlElementTypes {
    public static IElementType PYXL_TAG_PY_REFERENCE =
            new PyElementType("PYXL_TAG_PY_REFERENCE", PyxlTagPyReference.class);
    
    public static IElementType PYXL_STATEMENT =
            new PyElementType("PYXL_STATEMENT", PyxlStatement.class);

    public static IElementType PYXL_ATTRNAME = new PyElementType("PYXL_ATTRNAME");
}
