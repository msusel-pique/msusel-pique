package qatch.runnable;

import org.junit.Test;
import qatch.TestHelper;
import qatch.analysis.ITool;
import qatch.analysis.IToolLOC;
import qatch.model.QualityModel;

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
        // TODO PICKUP: write assertions, fix bugs
        System.out.println("...");
    }
}
