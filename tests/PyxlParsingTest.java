
import com.christofferklang.pyxl.parsing.PyxlParserDefinition;
import com.intellij.testFramework.ParsingTestCase;
import com.jetbrains.python.PythonDialectsTokenSetContributor;
import com.jetbrains.python.PythonTokenSetContributor;

public class PyxlParsingTest extends ParsingTestCase {
    public PyxlParsingTest() {
        super("", "py", new PyxlParserDefinition());
    }

    public void testParsingTestData() {
        doTest(true);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // Following lines learned from python plugin's test case - PythonParsingTest.java
        registerExtensionPoint(PythonDialectsTokenSetContributor.EP_NAME, PythonDialectsTokenSetContributor.class);
        registerExtension(PythonDialectsTokenSetContributor.EP_NAME, new PythonTokenSetContributor());

    }
    @Override
    protected String getTestDataPath() {
        return "../pycharm-pyxl/testData/";
    }

    @Override
    protected boolean skipSpaces() {
        return false;
    }

    @Override
    protected boolean includeRanges() {
        return true;
    }
}