package qatch.analysis;

import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class ToolTests {

    public class ToolInstance extends Tool implements ITool {
        ToolInstance(String name, Path toolConfig) { super(name, toolConfig); }

        @Override
        public Path analyze(Path projectLocation) { return null; }

        @Override
        public Map<String, Diagnostic> parse(Path toolResults) { return null; }
    }


    @Test
    public void testBuildMeasures() {
        Tool t = new ToolInstance("Test Tool", Paths.get("src/test/resources/config/test_tool_measures.yaml"));

        Finding f11 = new Finding("home/filepath11", 1, 11);
        Finding f21 = new Finding("home/filepath21", 2, 21);
        Finding f41 = new Finding("home/filepath41", 4, 41);
        Finding f42 = new Finding("home/filepath42", 41, 411);
        Finding f51 = new Finding("home/filepath51", 5, 51);

        Diagnostic scs0001 = new Diagnostic("Test Tool", "SCS0001");
        Diagnostic scs0002 = new Diagnostic("Test Tool", "SCS0002");
        Diagnostic scs0004 = new Diagnostic("Test Tool", "SCS0004");
        Diagnostic scs0005 = new Diagnostic("Test Tool", "SCS0005");

        scs0001.addFinding(f11);
        scs0002.addFinding(f21);
        scs0004.addFinding(f41);
        scs0004.addFinding(f42);
        scs0005.addFinding(f51);

        t.addDiagnostic(scs0001);
        t.addDiagnostic(scs0002);
        t.addDiagnostic(scs0004);
        t.addDiagnostic(scs0005);

        Map<String, Measure> m = t.buildMeasures();

        Assert.assertEquals(2, m.size());

        Assert.assertEquals(4, m.get("Injection").getDiagnostics().size());
        Assert.assertEquals(4, m.get("Cryptography").getDiagnostics().size());

        Assert.assertEquals(1, m.get("Injection").getDiagnostics().get(0).getFindings().size());
        Assert.assertEquals(0, m.get("Injection").getDiagnostics().get(3).getFindings().size());
        Assert.assertEquals(2, m.get("Cryptography").getDiagnostics().get(0).getFindings().size());
        Assert.assertEquals(0, m.get("Cryptography").getDiagnostics().get(2).getFindings().size());

        Assert.assertTrue(m.get("Injection").getDiagnostics().get(0).getFindings().contains(f11));
        Assert.assertTrue(m.get("Injection").getDiagnostics().get(1).getFindings().contains(f21));
        Assert.assertTrue(m.get("Cryptography").getDiagnostics().get(0).getFindings().contains(f41));
        Assert.assertTrue(m.get("Cryptography").getDiagnostics().get(0).getFindings().contains(f42));
        Assert.assertTrue(m.get("Cryptography").getDiagnostics().get(1).getFindings().contains(f51));
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
