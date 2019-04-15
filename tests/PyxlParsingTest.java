
import com.christofferklang.pyxl.parsing.PyxlParserDefinition;
import com.intellij.testFramework.ParsingTestCase;
import com.jetbrains.python.PythonDialectsTokenSetContributor;
import com.jetbrains.python.PythonTokenSetContributor;

public class PyxlParsingTest extends ParsingTestCase {
    /**
     * These tests work like this:
     * - They look at the name of the method, without the test-part to
     * figure out which files to use under testdata.
     *
     * I.e. adding a method called testMyTest() { doTest(true); } here
     * will run the file "testdata/MyTest.py" through the parser and expect the output
     * from that run to match the PSI tree decribed in "testdata/MyTest.txt".
     *
     * If the text file is not there on the first run of the test, one will be created
     * with the _current_ parser definition.
     */

    public void testParsingTestData() {
        doTest(true);
    }

    public void testnestedembed() {
        doTest(true);
    }

    public void testclass_self_ref() {
        doTest(true);
    }

    public void testWithStatements() {
        doTest(true);
    }

    public void testComments() {
        doTest(true);
    }

    public void testTagNames() {
        doTest(true);
    }

    public void testAttributes() {
        doTest(true);
    }

    public PyxlParsingTest() {
        super("", "py", new PyxlParserDefinition());
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
        return "./testdata/";
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
