
import com.christofferklang.pyxl.parsing.PyxlParserDefinition;
import com.intellij.testFramework.ParsingTestCase;

public class SimpleParsingTest extends ParsingTestCase {
    public SimpleParsingTest() {
        super("", "py", new PyxlParserDefinition());
    }

    public void testParsingTestData() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return "./tests/testData/";
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