package com.christofferklang.pyxl;

import com.christofferklang.pyxl.psi.PyxlArgumentList;
import com.christofferklang.pyxl.psi.PyxlAttrName;
import com.christofferklang.pyxl.psi.PyxlStatement;
import com.christofferklang.pyxl.psi.PyxlTagReference;
import com.intellij.psi.tree.IElementType;
import com.jetbrains.python.psi.PyElementType;

public class PyxlElementTypes {
    public static IElementType TAG_REFERENCE =
            new PyElementType("TAG_REFERENCE", PyxlTagReference.class);
    
    public static IElementType STATEMENT =
            new PyElementType("STATEMENT", PyxlStatement.class);

    public static IElementType ATTRNAME =
            new PyElementType("ATTRNAME", PyxlAttrName.class);

    public static IElementType ARGUMENT_LIST =
            new PyElementType("PYXL_ARGUMENT_LIST", PyxlArgumentList.class);
}
