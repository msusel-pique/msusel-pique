package qatch.analysis;

import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToolTests {

    public class ToolInstance extends Tool implements ITool {
        ToolInstance(String name, Path config) { super(name, config); }

        @Override
        public Path analyze(Path projectLocation) { return null; }

        @Override
        public Map<String, Diagnostic> parseAnalysis(Path toolResults) { return null; }

        public Path getConfig() { return null; }
    }


    @Test
    public void testApplyFindings() {
        Tool t = new ToolInstance("Test Tool", null);
        Map<String, Measure> measures = new HashMap<>();
        Map<String, Diagnostic> diagnosticFindings = new HashMap<>();

        Finding f11 = new Finding("home/filepath11", 1, 11);
        Finding f21 = new Finding("home/filepath21", 2, 21);
        Finding f41 = new Finding("home/filepath41", 4, 41);
        Finding f42 = new Finding("home/filepath42", 41, 411);
        Finding f51 = new Finding("home/filepath51", 5, 51);

        Diagnostic scs0001 = new Diagnostic("SCS0001");
        Diagnostic scs0002 = new Diagnostic("SCS0002");
        Diagnostic scs0004 = new Diagnostic("SCS0004");
        Diagnostic scs0005 = new Diagnostic("SCS0005");

        Diagnostic injDiag01 = new Diagnostic("SCS0001");
        Diagnostic injDiag02 = new Diagnostic("SCS0002");
        Diagnostic injDiag03 = new Diagnostic("Example no finding 1");
        Diagnostic injDiag04 = new Diagnostic("Example no finding 2");
        Diagnostic cryptoDiag04 = new Diagnostic( "SCS0004");
        Diagnostic cryptoDiag05 = new Diagnostic( "SCS0005");

        List<Diagnostic> injectionDiagnostics = Arrays.asList(injDiag01, injDiag02, injDiag03, injDiag04);
        List<Diagnostic> cryptoDiagnostics = Arrays.asList(cryptoDiag04, cryptoDiag05);

        Measure injection = new Measure("Injection", "Test Tool", null);
        Measure crypto = new Measure("Cryptography", "Test Tool", null);

        injection.setDiagnostics(injectionDiagnostics);
        crypto.setDiagnostics(cryptoDiagnostics);

        scs0001.setFinding(f11);
        scs0002.setFinding(f21);
        scs0004.setFinding(f41);
        scs0004.setFinding(f42);
        scs0005.setFinding(f51);

        measures.put("Injection", injection);
        measures.put("Cryptography", crypto);
        diagnosticFindings.put("SCS0001", scs0001);
        diagnosticFindings.put("SCS0002", scs0002);
        diagnosticFindings.put("SCS0004", scs0004);
        diagnosticFindings.put("SCS0005", scs0005);

        Map<String, Measure> m = t.applyFindings(measures, diagnosticFindings);

        Assert.assertEquals(2, m.size());

        Assert.assertEquals(4, m.get("Injection").getDiagnostics().size());
        Assert.assertEquals(2, m.get("Cryptography").getDiagnostics().size());

        Assert.assertEquals(1, m.get("Injection").getDiagnostics().get(0).getFindings().size());
        Assert.assertEquals(0, m.get("Injection").getDiagnostics().get(3).getFindings().size());
        Assert.assertEquals(2, m.get("Cryptography").getDiagnostics().get(0).getFindings().size());
        Assert.assertEquals(1, m.get("Cryptography").getDiagnostics().get(1).getFindings().size());

        Assert.assertTrue(m.get("Injection").getDiagnostics().get(0).getFindings().contains(f11));
        Assert.assertTrue(m.get("Injection").getDiagnostics().get(1).getFindings().contains(f21));
        Assert.assertTrue(m.get("Cryptography").getDiagnostics().get(0).getFindings().contains(f41));
        Assert.assertTrue(m.get("Cryptography").getDiagnostics().get(0).getFindings().contains(f42));
        Assert.assertTrue(m.get("Cryptography").getDiagnostics().get(1).getFindings().contains(f51));
    }

}
