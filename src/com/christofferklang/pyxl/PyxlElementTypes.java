package com.christofferklang.pyxl;

import com.christofferklang.pyxl.psi.PyTagClassReference;
import com.christofferklang.pyxl.psi.PyxlArgumentList;
import com.christofferklang.pyxl.psi.PyxlAttrName;
import com.christofferklang.pyxl.psi.PyxlTag;
import com.intellij.psi.tree.IElementType;
import com.jetbrains.python.psi.PyElementType;

public class PyxlElementTypes {
    public static IElementType TAG_REFERENCE =
            new PyElementType("TAG_REFERENCE", PyTagClassReference.class);
    
    public static IElementType TAG =
            new PyElementType("TAG", PyxlTag.class);

    public static IElementType ATTRNAME =
            new PyElementType("ATTRNAME", PyxlAttrName.class);

    public static IElementType ARGUMENT_LIST =
            new PyElementType("PYXL_ARGUMENT_LIST", PyxlArgumentList.class);
}
