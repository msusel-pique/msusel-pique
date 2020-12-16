package pique.utility;

import pique.analysis.ITool;
import pique.model.Diagnostic;
import pique.model.Finding;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class MockedIToolQmFull implements ITool {

    @Override
    public Path analyze(Path projectLocation) {
        return null;
    }

    /**
     * Diagnostic TST0011:
     *      - 1 finding of severity 1
     * Diagnostic TST0012:
     *      - 1 finding of severity 4
     */
    @Override
    public Map<String, Diagnostic> parseAnalysis(Path toolResults) {

        // Diagnostic TST0011:
        Finding f11 = new Finding("filepath", 1, 1, 1);
        Diagnostic d11 = new Diagnostic("TST0011", "Description from MockedITool", "toolName from MockedITool");
        d11.setChild(f11);

        // Diagnostic TST0012:
        Finding f12 = new Finding("filepath", 1, 1, 4);
        Diagnostic d12 = new Diagnostic("TST0012", "Description from MockedITool", "toolName from MockedITool");
        d12.setChild(f12);

        // Return diagnostics (now with attached findings)
        Map<String, Diagnostic> parseResults = new HashMap<>();
        parseResults.put(d11.getName(), d11);
        parseResults.put(d12.getName(), d12);

        return parseResults;
    }

    @Override
    public Path initialize(Path toolRoot) {
        return null;
    }

    @Override
    public String getName() {
        return "Mocked ITool QM Full";
    }
}
