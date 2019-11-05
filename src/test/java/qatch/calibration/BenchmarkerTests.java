package qatch.calibration;

import org.junit.Assert;
import org.junit.Test;
import qatch.TestHelper;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class BenchmarkerTests {

    private Benchmarker benchmarker = new Benchmarker(null, null);
    private Path analysisResults = Paths.get("src/test/resources/benchmark_results/properties.csv");

    @Test
    public void testGenerateThresholds() {
        benchmarker.setAnalysisResults(this.analysisResults);
        Map<String, Double[]> thresholds = this.benchmarker.generateThresholds(TestHelper.OUTPUT);


        Assert.assertEquals(3, thresholds.size());

        Assert.assertTrue(thresholds.containsKey("Property 01"));
        Assert.assertTrue(thresholds.containsKey("Property 02"));
        Assert.assertTrue(thresholds.containsKey("Property 03"));

        Assert.assertArrayEquals(thresholds.get("Property 01"), new Double[]{0.01, 0.019, 0.022});
        Assert.assertArrayEquals(thresholds.get("Property 02"), new Double[]{0.05, 0.068, 0.07});
        Assert.assertArrayEquals(thresholds.get("Property 03"), new Double[]{0.091, 0.093, 0.099});
    }
}
