package com.christofferklang.pyxl;

import com.christofferklang.pyxl.psi.*;
import com.intellij.psi.tree.IElementType;
import com.jetbrains.python.psi.PyElementType;

public class PyxlElementTypes {
    public static IElementType TAG_REFERENCE =
            new PyElementType("TAG_REFERENCE", PythonClassReference.class);
    
    public static IElementType TAG =
            new PyElementType("TAG", PyxlTag.class);

    public static IElementType ATTRNAME =
            new PyElementType("ATTRNAME", PyxlAttrName.class);

    public static IElementType ARGUMENT_LIST =
            new PyElementType("PYXL_ARGUMENT_LIST", PyxlArgumentList.class);

    public static IElementType MODULE_REFERENCE = new PyElementType("MODULE_REFERENCE", PyxlModuleReference.class);
}
