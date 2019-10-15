package qatch.analysis;

import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class ToolTests {

    public class ToolInstance extends Tool {
        ToolInstance(String name, Path toolConfig) { super(name, toolConfig); }

        @Override
        public Path analyze(Path projectLocation) { return null; }

        @Override
        public List<Measure> parse(Path toolResults) { return null; }
    }

    @Test
    public void testMapMeasure() {

        ITool t = new ToolInstance("Test Tool", Paths.get("src/test/resources/config/test_tool_measures.yaml"));
        Map<String, Measure> mappings = t.getMeasureMappings();

        Assert.assertEquals("Test Tool", mappings.get("Injection").getToolName());
        Assert.assertEquals("Test Tool", mappings.get("Cryptography").getToolName());
        Assert.assertEquals("Injection Findings", mappings.get("Injection").getName());
        Assert.assertEquals("Cryptography Findings", mappings.get("Cryptography").getName());
        Assert.assertEquals("SCS0001", mappings.get("Injection").getDiagnostics().get(0).getId());
        Assert.assertEquals("SCS0007", mappings.get("Injection").getDiagnostics().get(3).getId());
        Assert.assertEquals("SCS0004", mappings.get("Cryptography").getDiagnostics().get(0).getId());
        Assert.assertEquals("SCS0010", mappings.get("Cryptography").getDiagnostics().get(3).getId());
    }
}
