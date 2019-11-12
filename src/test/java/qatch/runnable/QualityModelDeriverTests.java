package qatch.runnable;

import org.junit.Assert;
import org.junit.Test;
import qatch.TestHelper;
import qatch.analysis.ITool;
import qatch.analysis.IToolLOC;
import qatch.model.Characteristic;
import qatch.model.Property;
import qatch.model.QualityModel;
import qatch.model.Tqi;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class QualityModelDeriverTests {

    @Test
    public void testDeriveModel() {

        // Configs
        Path qmDescriptionPath = Paths.get("src/test/resources/quality_models/qualityModel_test_description.json");
        Path benchmarkRepo = Paths.get("src/test/resources/benchmark_repository");
        Path comparisonMatricesDirectory = Paths.get("src/test/resources/comparison_matrices/test_derive_model");
        Path analysisResults = Paths.get("src/test/out/benchmark_results/benchmark_data.csv");
        Path rThresholdsOutput = Paths.get("src/test/out/r_thresholds");
        Path tempWeightsDirectory = Paths.get("src/test/out/weighter");
        String projectRootFlag = ".txt";

        // Initialize objects
        QualityModel qmDescription = new QualityModel(qmDescriptionPath);
        IToolLOC fakeLocTool = TestHelper.makeIToolLoc();
        ITool fakeTool = TestHelper.makeITool();
        Map<String, ITool> tools = new HashMap<String, ITool>() {{ put(fakeTool.getName(), fakeTool); }};

        // Run process
        QualityModel qm = QualityModelDeriver.deriveModel(
                qmDescription, fakeLocTool, tools, benchmarkRepo, comparisonMatricesDirectory, analysisResults,
                rThresholdsOutput, tempWeightsDirectory, projectRootFlag
        );

        // Assert
        Tqi tqi = qm.getTqi();
        Characteristic c1 = qm.getCharacteristic("Characteristic 01");
        Characteristic c2 = qm.getCharacteristic("Characteristic 02");
        Property p1 = qm.getProperty("Property 01");
        Property p2 = qm.getProperty("Property 02");

        Assert.assertEquals(0.6667, tqi.getWeight("Characteristic 01"), 0.0001);
        Assert.assertEquals(0.3333, tqi.getWeight("Characteristic 02"), 0.0001);

        Assert.assertEquals(0.25, c1.getWeight("Property 01"), 0.0001);
        Assert.assertEquals(0.75, c1.getWeight("Property 02"), 0.0001);
        Assert.assertEquals(0.8, c2.getWeight("Property 01"), 0.0001);
        Assert.assertEquals(0.2, c2.getWeight("Property 02"), 0.0001);

        Assert.assertArrayEquals(new Double[]{0.004, 0.004, 0.004}, p1.getThresholds());
        Assert.assertArrayEquals(new Double[]{0.006, 0.006, 0.006}, p2.getThresholds());
    }
}
