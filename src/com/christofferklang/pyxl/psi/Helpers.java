package com.christofferklang.pyxl.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.QualifiedName;
import com.jetbrains.python.psi.PyFile;
import com.jetbrains.python.psi.PyFromImportStatement;
import com.jetbrains.python.psi.PyImportElement;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

public class Helpers {
    public static final Set<String> PYXL_TAG_NAMES = new HashSet<String>();

    private static WeakHashMap<String, PyImportElement> sHtmlImportCache =
            new WeakHashMap<String, PyImportElement>();

    /**
     * Try and a find an import statement such as "from mymodule.pyxl import html"
     */
    public static PyImportElement getImportedPyxlHtmlModuleElementFromFile(PyFile pyFile) {
        final String cacheKey = String.format("%s:%s",
                pyFile.getContainingDirectory(),
                pyFile.getName());

        if (sHtmlImportCache.containsKey(cacheKey)) {
            PyImportElement importElement = sHtmlImportCache.get(cacheKey);
            if (importElement != null) {
                return importElement;
            }
        }

        List<PyFromImportStatement> imports = pyFile.getFromImports();

        for (PyFromImportStatement importStatement : imports) {
            // check for import statements that import from a "pyxl" package
            final QualifiedName qualifiedName = importStatement.getImportSourceQName();
            if (qualifiedName != null && "pyxl".equals(qualifiedName.getLastComponent())) {
                // check only for imports of the module "html"
                PyImportElement[] importedElements = importStatement.getImportElements();
                for (PyImportElement importedElement : importedElements) {
                    PsiElement htmlElement = importedElement.getElementNamed("html");
                    if (htmlElement instanceof PyFile) {
                        sHtmlImportCache.put(cacheKey, importedElement);
                        return importedElement;
                    }
                }
            }
        }

        return null;
    }

    /*********************
     * PYXL DEFAULT TAGS *
     *********************/

    static {
        // These are pulled from pyxl/html.py
        Helpers.PYXL_TAG_NAMES.add("x_a");
        Helpers.PYXL_TAG_NAMES.add("x_abbr");
        Helpers.PYXL_TAG_NAMES.add("x_acronym");
        Helpers.PYXL_TAG_NAMES.add("x_address");
        Helpers.PYXL_TAG_NAMES.add("x_area");
        Helpers.PYXL_TAG_NAMES.add("x_article");
        Helpers.PYXL_TAG_NAMES.add("x_aside");
        Helpers.PYXL_TAG_NAMES.add("x_audio");
        Helpers.PYXL_TAG_NAMES.add("x_b");
        Helpers.PYXL_TAG_NAMES.add("x_big");
        Helpers.PYXL_TAG_NAMES.add("x_blockquote");
        Helpers.PYXL_TAG_NAMES.add("x_body");
        Helpers.PYXL_TAG_NAMES.add("x_br");
        Helpers.PYXL_TAG_NAMES.add("x_button");
        Helpers.PYXL_TAG_NAMES.add("x_canvas");
        Helpers.PYXL_TAG_NAMES.add("x_caption");
        Helpers.PYXL_TAG_NAMES.add("x_cite");
        Helpers.PYXL_TAG_NAMES.add("x_code");
        Helpers.PYXL_TAG_NAMES.add("x_col");
        Helpers.PYXL_TAG_NAMES.add("x_colgroup");
        Helpers.PYXL_TAG_NAMES.add("x_cond_comment");
        Helpers.PYXL_TAG_NAMES.add("x_datalist");
        Helpers.PYXL_TAG_NAMES.add("x_dd");
        Helpers.PYXL_TAG_NAMES.add("x_del");
        Helpers.PYXL_TAG_NAMES.add("x_dfn");
        Helpers.PYXL_TAG_NAMES.add("x_div");
        Helpers.PYXL_TAG_NAMES.add("x_dl");
        Helpers.PYXL_TAG_NAMES.add("x_dt");
        Helpers.PYXL_TAG_NAMES.add("x_em");
        Helpers.PYXL_TAG_NAMES.add("x_embed");
        Helpers.PYXL_TAG_NAMES.add("x_fieldset");
        Helpers.PYXL_TAG_NAMES.add("x_figcaption");
        Helpers.PYXL_TAG_NAMES.add("x_figure");
        Helpers.PYXL_TAG_NAMES.add("x_footer");
        Helpers.PYXL_TAG_NAMES.add("x_form");
        Helpers.PYXL_TAG_NAMES.add("x_form_error");
        Helpers.PYXL_TAG_NAMES.add("x_frag");
        Helpers.PYXL_TAG_NAMES.add("x_frame");
        Helpers.PYXL_TAG_NAMES.add("x_frameset");
        Helpers.PYXL_TAG_NAMES.add("x_h1");
        Helpers.PYXL_TAG_NAMES.add("x_h2");
        Helpers.PYXL_TAG_NAMES.add("x_h3");
        Helpers.PYXL_TAG_NAMES.add("x_h4");
        Helpers.PYXL_TAG_NAMES.add("x_h5");
        Helpers.PYXL_TAG_NAMES.add("x_h6");
        Helpers.PYXL_TAG_NAMES.add("x_head");
        Helpers.PYXL_TAG_NAMES.add("x_header");
        Helpers.PYXL_TAG_NAMES.add("x_hr");
        Helpers.PYXL_TAG_NAMES.add("x_html");
// These tags are used by special pyxl syntax like <!-- --> etc
//        PYXL_TAG_NAMES.add("x_html_comment");
//        PYXL_TAG_NAMES.add("x_html_decl");
//        PYXL_TAG_NAMES.add("x_html_element");
//        PYXL_TAG_NAMES.add("x_html_element_nochild");
//        PYXL_TAG_NAMES.add("x_html_marked_decl");
//        PYXL_TAG_NAMES.add("x_html_ms_decl");
        Helpers.PYXL_TAG_NAMES.add("x_i");
        Helpers.PYXL_TAG_NAMES.add("x_iframe");
        Helpers.PYXL_TAG_NAMES.add("x_img");
        Helpers.PYXL_TAG_NAMES.add("x_input");
        Helpers.PYXL_TAG_NAMES.add("x_ins");
        Helpers.PYXL_TAG_NAMES.add("x_kbd");
        Helpers.PYXL_TAG_NAMES.add("x_label");
        Helpers.PYXL_TAG_NAMES.add("x_legend");
        Helpers.PYXL_TAG_NAMES.add("x_li");
        Helpers.PYXL_TAG_NAMES.add("x_link");
        Helpers.PYXL_TAG_NAMES.add("x_main");
        Helpers.PYXL_TAG_NAMES.add("x_map");
        Helpers.PYXL_TAG_NAMES.add("x_meta");
        Helpers.PYXL_TAG_NAMES.add("x_nav");
        Helpers.PYXL_TAG_NAMES.add("x_noframes");
        Helpers.PYXL_TAG_NAMES.add("x_noscript");
        Helpers.PYXL_TAG_NAMES.add("x_object");
        Helpers.PYXL_TAG_NAMES.add("x_ol");
        Helpers.PYXL_TAG_NAMES.add("x_optgroup");
        Helpers.PYXL_TAG_NAMES.add("x_option");
        Helpers.PYXL_TAG_NAMES.add("x_p");
        Helpers.PYXL_TAG_NAMES.add("x_param");
        Helpers.PYXL_TAG_NAMES.add("x_pre");
        Helpers.PYXL_TAG_NAMES.add("x_progress");
        Helpers.PYXL_TAG_NAMES.add("x_q");
// This tag is used to support the rawhtml() function.
//        PYXL_TAG_NAMES.add("x_rawhtml");
        Helpers.PYXL_TAG_NAMES.add("x_samp");
        Helpers.PYXL_TAG_NAMES.add("x_script");
        Helpers.PYXL_TAG_NAMES.add("x_section");
        Helpers.PYXL_TAG_NAMES.add("x_select");
        Helpers.PYXL_TAG_NAMES.add("x_small");
        Helpers.PYXL_TAG_NAMES.add("x_span");
        Helpers.PYXL_TAG_NAMES.add("x_strong");
        Helpers.PYXL_TAG_NAMES.add("x_style");
        Helpers.PYXL_TAG_NAMES.add("x_sub");
        Helpers.PYXL_TAG_NAMES.add("x_sup");
        Helpers.PYXL_TAG_NAMES.add("x_table");
        Helpers.PYXL_TAG_NAMES.add("x_tbody");
        Helpers.PYXL_TAG_NAMES.add("x_td");
        Helpers.PYXL_TAG_NAMES.add("x_textarea");
        Helpers.PYXL_TAG_NAMES.add("x_tfoot");
        Helpers.PYXL_TAG_NAMES.add("x_th");
        Helpers.PYXL_TAG_NAMES.add("x_thead");
        Helpers.PYXL_TAG_NAMES.add("x_time");
        Helpers.PYXL_TAG_NAMES.add("x_title");
        Helpers.PYXL_TAG_NAMES.add("x_tr");
        Helpers.PYXL_TAG_NAMES.add("x_tt");
        Helpers.PYXL_TAG_NAMES.add("x_u");
        Helpers.PYXL_TAG_NAMES.add("x_ul");
        Helpers.PYXL_TAG_NAMES.add("x_var");
        Helpers.PYXL_TAG_NAMES.add("x_video");
    }
}
