package com.christofferklang.pyxl;
import com.intellij.formatting.SpacingBuilder;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.jetbrains.python.formatter.PythonFormattingModelBuilder;

public class PyxlFormattingModelBuilder extends PythonFormattingModelBuilder {

    //we should be able to add Pyxl indentation information here. See also
    // http://confluence.jetbrains.com/display/IDEADEV/Developing+Custom+Language+Plugins+for+IntelliJ+IDEA#DevelopingCustomLanguagePluginsforIntelliJIDEA-CodeFormatter

    public PyxlFormattingModelBuilder() {
        super();
        System.out.println("Pyxl Formatting Model Builder");
    }


    @Override
    protected SpacingBuilder createSpacingBuilder(CodeStyleSettings settings) {
        SpacingBuilder foo = super.createSpacingBuilder(settings);
        return foo;
    }

}
