package pique.utility;

import pique.analysis.ITool;
import pique.evaluation.DefaultDiagnosticEvaluator;
import pique.model.Diagnostic;
import pique.model.Finding;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class MockedLocTool implements ITool {

    @Override
    public Path analyze(Path projectLocation) {
        return Paths.get("src/test/resources/tool_results/faketool_loc_output.txt");
    }

    @Override
    public Map<String, Diagnostic> parseAnalysis(Path toolResults) {
        Map<String, Diagnostic> diagnostics = new HashMap<>();
        Finding f1 = new Finding("no/path", 0, 0, 100);
        Diagnostic locDiagnostic = new Diagnostic("loc", "loc diagnostic description", this.getName(),
                new DefaultDiagnosticEvaluator());
        locDiagnostic.setChild(f1);
        diagnostics.put(locDiagnostic.getName(), locDiagnostic);
        return diagnostics;
    }

    @Override
    public Path initialize(Path toolRoot) {
        return null;
    }

    @Override
    public String getName() {
        return "Fake LoC Tool";
    }
}
