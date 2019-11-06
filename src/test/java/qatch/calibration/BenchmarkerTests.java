package qatch.calibration;

import org.junit.Assert;
import org.junit.Test;
import qatch.TestHelper;
import qatch.analysis.Diagnostic;
import qatch.analysis.ITool;
import qatch.analysis.IToolLOC;
import qatch.analysis.Measure;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class BenchmarkerTests {

    private Path analysisResults = Paths.get("src/test/resources/benchmark_results/properties.csv");
    private Path benchmarkRepo = Paths.get("src/test/resources/benchmark_repository");
    private Path qmDescription = Paths.get("src/test/resources/quality_models/qualityModel_test_description.json");

    @Test
    public void testAnalyze() {

        // Initialize mock tools
        IToolLOC fakeLocTool = new IToolLOC() {
            @Override
            public Integer analyze(Path projectLocation) {
                return 1000;
            }
        };
        ITool fakeTool = new ITool() {
            @Override
            public Path analyze(Path projectLocation) {
                return Paths.get("src/test/resources/tool_results/faketool_output.xml");
            }

            @Override
            public Map<String, Measure> applyFindings(Map<String, Measure> measures, Map<String, Diagnostic> diagnosticFindings) {
                return null;
            }

            @Override
            public Map<String, Measure> parseConfig(Path toolConfig) {
                return null;
            }

            @Override
            public Map<String, Diagnostic> parseAnalysis(Path toolResults) {
                Map<String, Diagnostic> diagnostics = new HashMap<>();
                diagnostics.put("TST0001", TestHelper.makeDiagnostic("TST0001"));
                diagnostics.put("TST0002", TestHelper.makeDiagnostic("TST0002"));
                diagnostics.put("TST0003", TestHelper.makeDiagnostic("TST0003"));
                diagnostics.put("TST0004", TestHelper.makeDiagnostic("TST0004"));
                diagnostics.put("TST0005", TestHelper.makeDiagnostic("TST0005"));
                return diagnostics;
            }

            @Override
            public Path getConfig() {
                return null;
            }

            @Override
            public String getName() {
                return "Fake Tool";
            }
        };
        Set<ITool> tools = new HashSet<>(Collections.singletonList(fakeTool));

        // Create benchmarker and run process
        Benchmarker benchmarker = new Benchmarker(benchmarkRepo, qmDescription, fakeLocTool, tools);
        benchmarker.analyze(".txt");

    }

    @Test
    public void testGenerateThresholds() {
        Benchmarker benchmarker = new Benchmarker();

        benchmarker.setAnalysisResults(this.analysisResults);
        Map<String, Double[]> thresholds = benchmarker.generateThresholds(TestHelper.OUTPUT);

        Assert.assertEquals(3, thresholds.size());

        Assert.assertTrue(thresholds.containsKey("Property 01"));
        Assert.assertTrue(thresholds.containsKey("Property 02"));
        Assert.assertTrue(thresholds.containsKey("Property 03"));

        Assert.assertArrayEquals(thresholds.get("Property 01"), new Double[]{0.01, 0.019, 0.022});
        Assert.assertArrayEquals(thresholds.get("Property 02"), new Double[]{0.05, 0.068, 0.07});
        Assert.assertArrayEquals(thresholds.get("Property 03"), new Double[]{0.091, 0.093, 0.099});
    }
}
