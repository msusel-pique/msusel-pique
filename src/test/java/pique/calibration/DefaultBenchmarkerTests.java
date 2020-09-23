package pique.calibration;

import com.opencsv.CSVReader;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import pique.TestHelper;
import pique.model.Finding;
import pique.model.Measure;
import pique.evaluation.Project;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultBenchmarkerTests {

    private Path analysisResultsAsInput = Paths.get("src/test/resources/benchmark_results/benchmark_data.csv");
    private Path analysisResultsAsOutput = Paths.get("src/test/out/benchmark_results/benchmark_data.csv");
    private Path benchmarkRepo = Paths.get("src/test/resources/benchmark_repository");
    private Path qmDescription = Paths.get("src/test/resources/quality_models/qualityModel_test_description.json");
    private Path rThresholdsOutput = Paths.get("src/test/out/r_thresholds");

    @Test
    public void testRThresholdRunnerMapper() {
        Map<String, Double[]> thresholds = DefaultBenchmarker.rThresholdRunnerMapper(TestHelper.OUTPUT, this.analysisResultsAsInput);

        Assert.assertEquals(3, thresholds.size());

        Assert.assertTrue(thresholds.containsKey("Property 01 measure"));
        Assert.assertTrue(thresholds.containsKey("Property 02 measure"));
        Assert.assertTrue(thresholds.containsKey("Property 03 measure"));

        Assert.assertArrayEquals(thresholds.get("Property 01 measure"), new Double[]{0.01, 0.019, 0.022});
        Assert.assertArrayEquals(thresholds.get("Property 02 measure"), new Double[]{0.05, 0.068, 0.07});
        Assert.assertArrayEquals(thresholds.get("Property 03 measure"), new Double[]{0.091, 0.093, 0.099});
    }
}
