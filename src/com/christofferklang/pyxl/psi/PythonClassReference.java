package com.christofferklang.pyxl.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.QualifiedName;
import com.jetbrains.python.psi.*;
import com.jetbrains.python.psi.impl.PyReferenceExpressionImpl;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

public class PythonClassReference extends PyReferenceExpressionImpl {
    private static final Set<String> EMPTY_HASH_SET = new HashSet<String>();
    private static final Set<String> PYXL_TAG_NAMES = new HashSet<String>();

    static {
        // These are pulled from pyxl/html.py
        PYXL_TAG_NAMES.add("x_a");
        PYXL_TAG_NAMES.add("x_abbr");
        PYXL_TAG_NAMES.add("x_acronym");
        PYXL_TAG_NAMES.add("x_address");
        PYXL_TAG_NAMES.add("x_area");
        PYXL_TAG_NAMES.add("x_article");
        PYXL_TAG_NAMES.add("x_aside");
        PYXL_TAG_NAMES.add("x_audio");
        PYXL_TAG_NAMES.add("x_b");
        PYXL_TAG_NAMES.add("x_big");
        PYXL_TAG_NAMES.add("x_blockquote");
        PYXL_TAG_NAMES.add("x_body");
        PYXL_TAG_NAMES.add("x_br");
        PYXL_TAG_NAMES.add("x_button");
        PYXL_TAG_NAMES.add("x_canvas");
        PYXL_TAG_NAMES.add("x_caption");
        PYXL_TAG_NAMES.add("x_cite");
        PYXL_TAG_NAMES.add("x_code");
        PYXL_TAG_NAMES.add("x_col");
        PYXL_TAG_NAMES.add("x_colgroup");
        PYXL_TAG_NAMES.add("x_cond_comment");
        PYXL_TAG_NAMES.add("x_datalist");
        PYXL_TAG_NAMES.add("x_dd");
        PYXL_TAG_NAMES.add("x_del");
        PYXL_TAG_NAMES.add("x_dfn");
        PYXL_TAG_NAMES.add("x_div");
        PYXL_TAG_NAMES.add("x_dl");
        PYXL_TAG_NAMES.add("x_dt");
        PYXL_TAG_NAMES.add("x_em");
        PYXL_TAG_NAMES.add("x_embed");
        PYXL_TAG_NAMES.add("x_fieldset");
        PYXL_TAG_NAMES.add("x_figcaption");
        PYXL_TAG_NAMES.add("x_figure");
        PYXL_TAG_NAMES.add("x_footer");
        PYXL_TAG_NAMES.add("x_form");
        PYXL_TAG_NAMES.add("x_form_error");
        PYXL_TAG_NAMES.add("x_frag");
        PYXL_TAG_NAMES.add("x_frame");
        PYXL_TAG_NAMES.add("x_frameset");
        PYXL_TAG_NAMES.add("x_h1");
        PYXL_TAG_NAMES.add("x_h2");
        PYXL_TAG_NAMES.add("x_h3");
        PYXL_TAG_NAMES.add("x_h4");
        PYXL_TAG_NAMES.add("x_h5");
        PYXL_TAG_NAMES.add("x_h6");
        PYXL_TAG_NAMES.add("x_head");
        PYXL_TAG_NAMES.add("x_header");
        PYXL_TAG_NAMES.add("x_hr");
        PYXL_TAG_NAMES.add("x_html");
        PYXL_TAG_NAMES.add("x_html_comment");
        PYXL_TAG_NAMES.add("x_html_decl");
        PYXL_TAG_NAMES.add("x_html_element");
        PYXL_TAG_NAMES.add("x_html_element_nochild");
        PYXL_TAG_NAMES.add("x_html_marked_decl");
        PYXL_TAG_NAMES.add("x_html_ms_decl");
        PYXL_TAG_NAMES.add("x_i");
        PYXL_TAG_NAMES.add("x_iframe");
        PYXL_TAG_NAMES.add("x_img");
        PYXL_TAG_NAMES.add("x_input");
        PYXL_TAG_NAMES.add("x_ins");
        PYXL_TAG_NAMES.add("x_kbd");
        PYXL_TAG_NAMES.add("x_label");
        PYXL_TAG_NAMES.add("x_legend");
        PYXL_TAG_NAMES.add("x_li");
        PYXL_TAG_NAMES.add("x_link");
        PYXL_TAG_NAMES.add("x_main");
        PYXL_TAG_NAMES.add("x_map");
        PYXL_TAG_NAMES.add("x_meta");
        PYXL_TAG_NAMES.add("x_nav");
        PYXL_TAG_NAMES.add("x_noframes");
        PYXL_TAG_NAMES.add("x_noscript");
        PYXL_TAG_NAMES.add("x_object");
        PYXL_TAG_NAMES.add("x_ol");
        PYXL_TAG_NAMES.add("x_optgroup");
        PYXL_TAG_NAMES.add("x_option");
        PYXL_TAG_NAMES.add("x_p");
        PYXL_TAG_NAMES.add("x_param");
        PYXL_TAG_NAMES.add("x_pre");
        PYXL_TAG_NAMES.add("x_progress");
        PYXL_TAG_NAMES.add("x_q");
        PYXL_TAG_NAMES.add("x_rawhtml");
        PYXL_TAG_NAMES.add("x_samp");
        PYXL_TAG_NAMES.add("x_script");
        PYXL_TAG_NAMES.add("x_section");
        PYXL_TAG_NAMES.add("x_select");
        PYXL_TAG_NAMES.add("x_small");
        PYXL_TAG_NAMES.add("x_span");
        PYXL_TAG_NAMES.add("x_strong");
        PYXL_TAG_NAMES.add("x_style");
        PYXL_TAG_NAMES.add("x_sub");
        PYXL_TAG_NAMES.add("x_sup");
        PYXL_TAG_NAMES.add("x_table");
        PYXL_TAG_NAMES.add("x_tbody");
        PYXL_TAG_NAMES.add("x_td");
        PYXL_TAG_NAMES.add("x_textarea");
        PYXL_TAG_NAMES.add("x_tfoot");
        PYXL_TAG_NAMES.add("x_th");
        PYXL_TAG_NAMES.add("x_thead");
        PYXL_TAG_NAMES.add("x_time");
        PYXL_TAG_NAMES.add("x_title");
        PYXL_TAG_NAMES.add("x_tr");
        PYXL_TAG_NAMES.add("x_tt");
        PYXL_TAG_NAMES.add("x_u");
        PYXL_TAG_NAMES.add("x_ul");
        PYXL_TAG_NAMES.add("x_var");
        PYXL_TAG_NAMES.add("x_video");
    }

    private static Set<String> mCachedSpecialPyxlTagNames = null;
    private static WeakHashMap<String, PyImportElement> sHtmlImportCache =
            new WeakHashMap<String, PyImportElement>();

    public PythonClassReference(ASTNode astNode) {
        super(astNode);
    }

    @Nullable
    @Override
    public String getReferencedName() {
        return pyxlClassName(getNode().getText());
    }

    @Nullable
    @Override
    public PyExpression getQualifier() {
        PyExpression realQualifier = super.getQualifier();
        if(realQualifier == null && isPyxlHtmlTag(getReferencedName())) {
            // Implicitly assume the tag is a reference to a pyxl html tag if the pyxl html module is imported and we
            // aren't using a qualifier already. This will break resolution of tags defined in a more local scope than
            // pyxl.html (e.g. if you make your own class x_div in a file that also imports pyxl.html).
            // This is consistent with how Pyxl works:
            // https://github.com/dropbox/pyxl/blob/daa01ca026ef3dba931d3ba56118ad8f8f6bec94/pyxl/codec/parser.py#L211
            PyImportElement pyxlHtmlImportElement = getImportedPyxlHtmlModuleElement();
            if(pyxlHtmlImportElement != null) {
                return pyxlHtmlImportElement.getImportReferenceExpression();
            }
        }
        return realQualifier;
    }

    private boolean isPyxlHtmlTag(String name) {
        return PYXL_TAG_NAMES.contains(name);
        // Uncomment the line below to get the "live" set of Pyxl tags.
        // return getSpecialPyxlTagsFromImportedHtmlModule().contains(name);
    }

    @SuppressWarnings("UnusedDeclaration")
    private Set<String> getSpecialPyxlTagsFromImportedHtmlModule() {
        if(mCachedSpecialPyxlTagNames == null) {
            PyImportElement importPyxlHtmlElement = getImportedPyxlHtmlModuleElement();
            if(importPyxlHtmlElement != null) {
                PyFile htmlModule = (PyFile) importPyxlHtmlElement.getElementNamed("html");

                mCachedSpecialPyxlTagNames = new HashSet<String>();
                //noinspection ConstantConditions
                for(PyClass topLevelClass : htmlModule.getTopLevelClasses()) {
                    mCachedSpecialPyxlTagNames.add(topLevelClass.getName());
                }

                // Consider transient classes in the top level scope as well
                for(PyFromImportStatement importStatement : htmlModule.getFromImports()) {
                    for(PyImportElement importElement : importStatement.getImportElements()) {
                        final String visibleName = importElement.getVisibleName();
                        if(visibleName != null && visibleName.startsWith("x_")) {
                            // Just swallowing all import classes starting with
                            // x_ isn't *technically* correct (any class can be named x_), but
                            // definitely good enough for our purposes.
                            mCachedSpecialPyxlTagNames.add(importElement.getVisibleName());
                        }
                    }
                }
            }
        }

        return mCachedSpecialPyxlTagNames == null ? EMPTY_HASH_SET : mCachedSpecialPyxlTagNames;
    }

    /**
     * Try and a find an import statement such as "from mymodule.pyxl import html"
     */
    private PyImportElement getImportedPyxlHtmlModuleElement() {
        if(!(getContainingFile() instanceof PyFile)) return null; // not a python file

        final PyFile pyFile = (PyFile) getContainingFile();
        final String cacheKey = String.format("%s:%s",
                pyFile.getContainingDirectory(),
                pyFile.getName());

        if(sHtmlImportCache.containsKey(cacheKey)) {
            PyImportElement importElement = sHtmlImportCache.get(cacheKey);
            if(importElement != null) {
                return importElement;
            }
        }

        List<PyFromImportStatement> imports = pyFile.getFromImports();

        for(PyFromImportStatement importStatement : imports) {
            // check for import statements that import from a "pyxl" package
            final QualifiedName qualifiedName = importStatement.getImportSourceQName();
            if(qualifiedName != null && "pyxl".equals(qualifiedName.getLastComponent())) {
                // check only for imports of the module "html"
                PyImportElement[] importedElements = importStatement.getImportElements();
                for(PyImportElement importedElement : importedElements) {
                    PsiElement htmlElement = importedElement.getElementNamed("html");
                    if(htmlElement instanceof PyFile) {
                        sHtmlImportCache.put(cacheKey, importedElement);
                        return importedElement;
                    }
                }
            }
        }

        return null;
    }

    private String pyxlClassName(String tagName) {
        if(tagName.indexOf(".") > 0) {
            // tag contains a module reference like: <module.pyxl_class>
            final StringBuilder qualifiedTagName = new StringBuilder(tagName);
            final int offset = qualifiedTagName.lastIndexOf(".");
            tagName = qualifiedTagName.subSequence(offset + 1, qualifiedTagName.length()).toString();
            return "x_" + tagName;
        }
        return "x_" + tagName;
    }

    @Override
    public String toString() {
        return "PyClassTagReference: " + getReferencedName();
    }
}
