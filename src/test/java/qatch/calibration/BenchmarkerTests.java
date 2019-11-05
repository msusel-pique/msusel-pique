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
import java.util.Map;

public class BenchmarkerTests {

    private Path analysisResults = Paths.get("src/test/resources/benchmark_results/properties.csv");

    @Test
    public void testAnalyze() {

        ITool fakeTool = new ITool() {
            @Override
            public Path analyze(Path projectLocation) {
                return null;
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
                return null;
            }

            @Override
            public Path getConfig() {
                return null;
            }

            @Override
            public String getName() {
                return null;
            }
        };
        IToolLOC fakeLocTool = new IToolLOC() {
            @Override
            public Integer analyze(Path projectLocation) {
                return 1000;
            }
        };


    }

    @Test
    public void testGenerateThresholds() {
        Benchmarker benchmarker = new Benchmarker(null, null);

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
