package com.christofferklang.pyxl;

import com.christofferklang.pyxl.psi.*;
import com.intellij.psi.tree.IElementType;
import com.jetbrains.python.psi.PyElementType;

public class PyxlElementTypes {
    public static IElementType PYCLASS_REF =
            new PyElementType("PYCLASS REF", PyClassReference.class);

    public static IElementType PYXL_CLASS_INIT_CALL =
            new PyElementType("PYXL CLASS INIT CALL", PyxlClassInitCallExpression.class);

    public static IElementType PYXL_INSTANCE_CALL =
            new PyElementType("PYXL INSTANCE CALL", PyxlInstanceCallExpression.class);

    public static IElementType COND_TAG =
            new PyElementType("COND_TAG", PyxlConditionalTag.class);

    public static IElementType ATTRNAME =
            new PyElementType("ATTRNAME", PyxlAttrName.class);

    public static IElementType ARGUMENT_LIST =
            new PyElementType("PYXL_ARGUMENT_LIST", PyxlArgumentList.class);

    public static IElementType MODULE_REFERENCE = new PyElementType("MODULE_REFERENCE", PyxlModuleReference.class);
}
