package qatch.calibration;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class BenchmarkerTests {

    private Benchmarker benchmarker = new Benchmarker(null, null);
    private Path analysisResults = Paths.get("src/test/resources/benchmark_results/properties.csv");

    @Test
    public void testGenerateThresholds() {
        benchmarker.setAnalysisResults(this.analysisResults);
        Map<String, Double[]> thresholds = benchmarker.generateThresholds();
    }
}
