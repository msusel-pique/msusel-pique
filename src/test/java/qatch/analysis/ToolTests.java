package qatch.analysis;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class ToolTests {

    private Path tempTestOutputDirectory = Paths.get("src/test/out");
    private Path testToolLocation = Paths.get("src/test/resources/fake_tools/Test Tool");
    private Tool testTool;


    @Before
    public void setUp() {
        testTool = new Tool("Test Tool", testToolLocation, tempTestOutputDirectory) {
            @Override
            public Path analyze(Path projectLocation) { return null; }
            @Override
            public Map<String, Diagnostic> parseAnalysis(Path toolResults) { return null; }
        };
    }

    /**
     * Test construction and private extract to temp resource folder method
     */
    @Test
    public void testToolConstructor() {
        Path tempResourcesFolder = testTool.getLocation();
        Path tempTestTool = Paths.get(tempResourcesFolder.toString(), testTool.getName());
        File testToolFile01 = new File(tempTestTool.toString(), "test_tool_file_01.txt");
        File testToolFile02 = new File(tempTestTool.toString(), "test_tool_file_02.txt");

        Assert.assertTrue(tempResourcesFolder.toFile().exists());
        Assert.assertTrue(tempTestTool.toFile().exists());
        Assert.assertTrue(testToolFile01.exists());
        Assert.assertTrue(testToolFile01.isFile());
        Assert.assertTrue(testToolFile02.exists());
        Assert.assertTrue(testToolFile02.isFile());

    }

    @Test
    public void testGetLocation() {
        Path tempToolLocation = testTool.getLocation();

        Assert.assertEquals(tempTestOutputDirectory, tempToolLocation.getParent());
        Assert.assertTrue(tempToolLocation.getFileName().toString().startsWith(testTool.getName().replaceAll("\\s","")));
    }
}
