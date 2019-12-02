package qatch.analysis;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import qatch.utility.FileUtility;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class ToolTests {

    private Path tempTestOutputDirectory = Paths.get("src/test/out");
    private Path testToolLocation = Paths.get("src/test/resources/fake_tools/Test Tool");
    private Tool testTool;

    class ToolInstance extends Tool {
        public ToolInstance(String name, Path toolRoot) { super(name, toolRoot); }
        @Override
        public Path analyze(Path projectLocation) {
            return null;
        }
        @Override
        public Map<String, Diagnostic> parseAnalysis(Path toolResults) {
            return null;
        }
        @Override
        public Path initialize(Path toolRoot) {
            Path toolRootTempFolder = FileUtility.extractResourcesAsIde(tempTestOutputDirectory, toolRoot);
            setToolRoot(Paths.get(toolRootTempFolder.toString(), "Test Tool"));
            return Paths.get(getToolRoot().toString(), "test_tool_file_01.txt");
        }
    }

    @Before
    public void setUp() {
        testTool = new ToolInstance("Test Tool", testToolLocation);
    }

    /**
     * Test construction and private extract to temp resource folder method
     */
    @Test
    public void testToolConstructorWithTempFolderExtraction() {
        Path tempTestTool = testTool.getToolRoot();
        File testToolFile01 = new File(tempTestTool.toString(), "test_tool_file_01.txt");
        File testToolFile02 = new File(tempTestTool.toString(), "test_tool_file_02.txt");

        Assert.assertTrue(tempTestTool.toFile().exists());
        Assert.assertTrue(testToolFile01.exists());
        Assert.assertTrue(testToolFile01.isFile());
        Assert.assertTrue(testToolFile02.exists());
        Assert.assertTrue(testToolFile02.isFile());

    }

    @Test
    public void testGetToolRoot() {
        Path tempToolLocation = testTool.getToolRoot();

        Assert.assertEquals(tempTestOutputDirectory, tempToolLocation.getParent().getParent());
        Assert.assertTrue(tempToolLocation.getFileName().toString().startsWith(testTool.getName()));
    }
}
