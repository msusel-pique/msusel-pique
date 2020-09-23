package pique.thesis_tests;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import pique.TestHelper;
import pique.calibration.IWeighter;
import pique.calibration.WeightResult;
import pique.calibration.AHPWeighter;
import pique.evaluation.DefaultDiagnosticEvaluator;
import pique.evaluation.LOCDiagnosticEvaluator;
import pique.model.Diagnostic;
import pique.model.Finding;
import pique.analysis.ITool;
import pique.model.ProductFactor;
import pique.model.QualityAspect;
import pique.model.QualityModel;
import pique.model.Tqi;
import pique.runnable.QualityModelDeriver;
import pique.runnable.SingleProjectEvaluator;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Category(ThesisTest.class)
public class ThesisTests {

    Path PROJECT_DIR = Paths.get("src/test/resources/thesis_tests/FakeProject_01");
    Path RESULTS_DIR = Paths.get("src/test/thesis_out/SingleProjEval");
    Path QM_LOCATION = Paths.get("src/test/resources/thesis_tests/quality_models/qualityModel_test.json");
    Path TOOL_RESULTS = Paths.get("src/test/resources/thesis_tests/tool_results");
    Path TEST_OUT = Paths.get("src/test/thesis_out");

    Path qmDescriptionPath = Paths.get("src/test/resources/thesis_tests/quality_models/qualityModel_test_description.json");
    Path benchmarkRepo = Paths.get("src/test/resources/thesis_tests/benchmark_repository");
    Path comparisonMatricesDirectory = Paths.get("src/test/resources/thesis_tests/comparison_matrices/test_derive_model");
    Path analysisResults = Paths.get("src/test/thesis_out/benchmark_results/benchmark_data.csv");
    Path rThresholdsOutput = Paths.get("src/test/thesis_out/r_thresholds");
    Path tempWeightsDirectory = Paths.get("src/test/thesis_out/weighter");
    String projectRootFlag = ".txt";

    @Test
    public void testRunEvaluator() throws IOException {

        // Initialize tools
        Set<ITool> tools = Stream.of(TestHelper.makeITool(), TestHelper.makeIToolLoc()).collect(Collectors.toSet());

        // Run single project evaluator
        SingleProjectEvaluator spe = new SingleProjectEvaluator();
        Path result = spe.runEvaluator(PROJECT_DIR, RESULTS_DIR, QM_LOCATION, tools);

        // Read result file
        FileReader fr = new FileReader(result.toString());
        JsonObject jsonResults = new JsonParser().parse(fr).getAsJsonObject();
        fr.close();

        JsonObject additionalData = jsonResults.getAsJsonObject("additionalData");
        JsonObject jsonFactors = jsonResults.getAsJsonObject("factors");
        JsonObject jsonMeasures = jsonResults.getAsJsonObject("measures");

        JsonObject jsonTqi = jsonFactors.getAsJsonObject("tqi");

        JsonObject jsonQualAspects = jsonFactors.getAsJsonObject("quality_aspects");
        JsonObject jsonQa_01 = jsonQualAspects.getAsJsonObject("QualityAspect 01");
        JsonObject jasonQa_02 = jsonQualAspects.getAsJsonObject("QualityAspect 02");

        JsonObject jsonProdFactors = jsonFactors.getAsJsonObject("product_factors");
        JsonObject jsonProdFact_01 = jsonProdFactors.getAsJsonObject("ProductFactor 01");
        JsonObject jsonProdFact_02 = jsonProdFactors.getAsJsonObject("ProductFactor 02");

        JsonObject jsonMeasure01 = jsonMeasures.getAsJsonObject("Measure 01");
        JsonObject jsonMeasure02 = jsonMeasures.getAsJsonObject("Measure 02");

        // Asserts
        Assert.assertEquals("FakeProject_01", additionalData.getAsJsonPrimitive("projectName").getAsString());
        Assert.assertEquals("200", additionalData.getAsJsonPrimitive("projectLinesOfCode").getAsString());

        Assert.assertEquals("Test QM", jsonResults.getAsJsonPrimitive("name").getAsString());

        JsonObject jsonTotalQuality = jsonTqi.getAsJsonObject("Total Quality");
        Assert.assertEquals(0.586875, jsonTotalQuality.getAsJsonPrimitive("value").getAsFloat(), 0.0001);
        Assert.assertEquals(0.8, jsonTotalQuality.getAsJsonObject("weights").getAsJsonPrimitive("QualityAspect 01").getAsDouble(), 0.0001);
        Assert.assertEquals(0.2, jsonTotalQuality.getAsJsonObject("weights").getAsJsonPrimitive("QualityAspect 02").getAsDouble(), 0.0001);

        Assert.assertEquals(0.58125, jsonQa_01.getAsJsonPrimitive("value").getAsFloat(), 0.0001);
        Assert.assertEquals(0.6, jsonQa_01.getAsJsonObject("weights").getAsJsonPrimitive("ProductFactor 01").getAsDouble(), 0.0001);
        Assert.assertEquals(0.4, jsonQa_01.getAsJsonObject("weights").getAsJsonPrimitive("ProductFactor 02").getAsDouble(), 0.0001);

        Assert.assertEquals(0.609375, jasonQa_02.getAsJsonPrimitive("value").getAsFloat(), 0.0001);
        Assert.assertEquals(0.5, jasonQa_02.getAsJsonObject("weights").getAsJsonPrimitive("ProductFactor 01").getAsDouble(), 0.0001);
        Assert.assertEquals(0.5, jasonQa_02.getAsJsonObject("weights").getAsJsonPrimitive("ProductFactor 02").getAsDouble(), 0.0001);

        Assert.assertEquals(0.46875, jsonProdFact_01.getAsJsonPrimitive("value").getAsFloat(), 0.0001);
        Assert.assertEquals(0.75, jsonProdFact_02.getAsJsonPrimitive("value").getAsFloat(), 0.0001);

        Assert.assertEquals(0.46875, jsonMeasure01.getAsJsonPrimitive("value").getAsFloat(), 0.0001);
        Assert.assertEquals(0.0, jsonMeasure01.getAsJsonArray("thresholds").get(0).getAsFloat(), 0.0001);
        Assert.assertEquals(0.004, jsonMeasure01.getAsJsonArray("thresholds").get(1).getAsFloat(), 0.0001);
        Assert.assertEquals(0.02, jsonMeasure01.getAsJsonArray("thresholds").get(2).getAsFloat(), 0.0001);

        Assert.assertEquals(0.75, jsonMeasure02.getAsJsonPrimitive("value").getAsFloat(), 0.0001);
        Assert.assertEquals(0.0, jsonMeasure02.getAsJsonArray("thresholds").get(0).getAsFloat(), 0.0001);
        Assert.assertEquals(0.01, jsonMeasure02.getAsJsonArray("thresholds").get(1).getAsFloat(), 0.0001);
        Assert.assertEquals(0.02, jsonMeasure02.getAsJsonArray("thresholds").get(2).getAsFloat(), 0.0001);
    }

    @Test
    public void testDeriveModel() {

        // Initialize objects
        QualityModel qmDescription = new QualityModel(qmDescriptionPath);

        // loc diagnostic returns 50 lines of code
        ITool fakeLocTool = new ITool() {
            @Override
            public Path analyze(Path projectLocation) {
                return Paths.get("src/test/resources/tool_results/faketool_loc_output.txt");
            }
            @Override
            public Map<String, Diagnostic> parseAnalysis(Path toolResults) {
                Map<String, Diagnostic> diagnostics = new HashMap<>();
                Finding f1 = new Finding(50);
                Diagnostic locDiagnostic = new Diagnostic("loc", "loc diagnostic description", this.getName(), new LOCDiagnosticEvaluator());
                locDiagnostic.setFinding(f1);
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
        };

        // (Make ITool to return different values for each project)
        ITool tool = new ITool() {
            @Override
            public Path analyze(Path projectLocation) {
                switch (projectLocation.getFileName().toString()) {
                    case "BenchmarkProjectOne":
                        return Paths.get("src/test/resources/tool_results/benchmark_one_results.xml");
                    case "BenchmarkProjectTwo":
                        return Paths.get("src/test/resources/tool_results/benchmark_two_results.xml");
                    case "BenchmarkProjectThree":
                        return Paths.get("src/test/resources/tool_results/benchmark_three_results.xml");
                    default:
                        throw new RuntimeException("switch statement default case");
                }
            }

            /*
             * Test info for reference and assertions:
             *
             * Project 01:
             *      Measure 01, TST0001: 1 finding
             *      Measure 02, TST0003: 1 finding
             * Project 02:
             *      Measure 01, TST0001: 1 finding
             *      Measure 01, TST0002: 1 finding
             *      Measure 02, TST0003: 1 finding
             *      Measure 02, TST0004: 1 finding
             *      Measure 02, TST0005: 1 finding
             * Project 03:
             *      Measure 01, TST0001: 2 findings
             *      Measure 01, TST0002: 2 findings
             *      Measure 02, TST0003: 2 findings
             *      Measure 02, TST0004: 2 findings
             *      Measure 02, TST0005: 2 findings
             */
            @Override
            public Map<String, Diagnostic> parseAnalysis(Path toolResults) {
                Finding f1 = TestHelper.makeFinding("file/path/one", 11, 1);
                Finding f2 = TestHelper.makeFinding("file/path/two", 22, 2);

                Map<String, Diagnostic> diagnostics = new HashMap<>();
                switch (toolResults.getFileName().toString()) {
                    case "benchmark_one_results.xml":

                        Diagnostic bench1tst1 = new Diagnostic("TST0001", "Sample Description", "Sample Tool Name", new DefaultDiagnosticEvaluator());
                        Diagnostic bench1tst3 = new Diagnostic("TST0003", "Sample Description", "Sample Tool Name", new DefaultDiagnosticEvaluator());
                        bench1tst1.setFinding(f1);
                        bench1tst3.setFinding(f1);

                        diagnostics.put("TST0001", bench1tst1);
                        diagnostics.put("TST0003", bench1tst3);

                        return diagnostics;

                    case "benchmark_two_results.xml":
                        Diagnostic bench2tst1 = new Diagnostic("TST0001", "Sample Description", "Sample Tool Name", new DefaultDiagnosticEvaluator());
                        Diagnostic bench2tst2 = new Diagnostic("TST0002", "Sample Description", "Sample Tool Name", new DefaultDiagnosticEvaluator());
                        Diagnostic bench2tst3 = new Diagnostic("TST0003", "Sample Description", "Sample Tool Name", new DefaultDiagnosticEvaluator());
                        Diagnostic bench2tst4 = new Diagnostic("TST0004", "Sample Description", "Sample Tool Name", new DefaultDiagnosticEvaluator());
                        Diagnostic bench2tst5 = new Diagnostic("TST0005", "Sample Description", "Sample Tool Name", new DefaultDiagnosticEvaluator());

                        bench2tst1.setFinding(f1);
                        bench2tst2.setFinding(f1);
                        bench2tst3.setFinding(f1);
                        bench2tst4.setFinding(f1);
                        bench2tst5.setFinding(f1);

                        diagnostics.put("TST0001", bench2tst1);
                        diagnostics.put("TST0002", bench2tst2);
                        diagnostics.put("TST0003", bench2tst3);
                        diagnostics.put("TST0004", bench2tst4);
                        diagnostics.put("TST0005", bench2tst5);

                        return diagnostics;

                    case "benchmark_three_results.xml":
                        Diagnostic bench3tst1 = new Diagnostic("TST0001", "Sample Description", "Sample Tool Name", new DefaultDiagnosticEvaluator());
                        Diagnostic bench3tst2 = new Diagnostic("TST0002", "Sample Description", "Sample Tool Name", new DefaultDiagnosticEvaluator());
                        Diagnostic bench3tst3 = new Diagnostic("TST0003", "Sample Description", "Sample Tool Name", new DefaultDiagnosticEvaluator());
                        Diagnostic bench3tst4 = new Diagnostic("TST0004", "Sample Description", "Sample Tool Name", new DefaultDiagnosticEvaluator());
                        Diagnostic bench3tst5 = new Diagnostic("TST0005", "Sample Description", "Sample Tool Name", new DefaultDiagnosticEvaluator());

                        bench3tst1.setFindings(Stream.of(f1, f2).collect(Collectors.toSet()));
                        bench3tst2.setFindings(Stream.of(f1, f2).collect(Collectors.toSet()));
                        bench3tst3.setFindings(Stream.of(f1, f2).collect(Collectors.toSet()));
                        bench3tst4.setFindings(Stream.of(f1, f2).collect(Collectors.toSet()));
                        bench3tst5.setFindings(Stream.of(f1, f2).collect(Collectors.toSet()));

                        diagnostics.put("TST0001", bench3tst1);
                        diagnostics.put("TST0002", bench3tst2);
                        diagnostics.put("TST0003", bench3tst3);
                        diagnostics.put("TST0004", bench3tst4);
                        diagnostics.put("TST0005", bench3tst5);

                        return diagnostics;

                    default:
                        throw new RuntimeException("switch statement default case");
                }
            }

            @Override
            public Path initialize(Path toolRoot) {
                return null;
            }

            @Override
            public String getName() {
                return "Deriver Test Tool";
            }
        };

        Set<ITool> tools = Stream.of(tool, fakeLocTool).collect(Collectors.toSet());

        // Run process
        QualityModel qm = QualityModelDeriver.deriveModel(
                qmDescription, tools, benchmarkRepo, comparisonMatricesDirectory, analysisResults,
                rThresholdsOutput, tempWeightsDirectory, projectRootFlag
        );

        // Save to file
        qm.exportToJson(TEST_OUT);

        // Assert
        Tqi tqi = qm.getTqi();
        QualityAspect c1 = qm.getQualityAspect("QualityAspect 01");
        QualityAspect c2 = qm.getQualityAspect("QualityAspect 02");
        ProductFactor p1 = qm.getProductFactor("ProductFactor 01");
        ProductFactor p2 = qm.getProductFactor("ProductFactor 02");

        Assert.assertEquals(0.9, tqi.getWeight("QualityAspect 01"), 0.0001);
        Assert.assertEquals(0.1, tqi.getWeight("QualityAspect 02"), 0.0001);

        Assert.assertEquals(0.5, c1.getWeight("ProductFactor 01"), 0.0001);
        Assert.assertEquals(0.5, c1.getWeight("ProductFactor 02"), 0.0001);
        Assert.assertEquals(0.2, c2.getWeight("ProductFactor 01"), 0.0001);
        Assert.assertEquals(0.8, c2.getWeight("ProductFactor 02"), 0.0001);

        Assert.assertArrayEquals(new Double[]{0.02, 0.04, 0.12}, p1.getMeasure().getThresholds());
        Assert.assertArrayEquals(new Double[]{0.02, 0.06, 0.18}, p2.getMeasure().getThresholds());
    }

    @Test
    public void testQualityModel() {
        QualityModel qm = new QualityModel(QM_LOCATION);
        Assert.assertEquals("Test QM", qm.getName());
    }

    @Test
    public void testWeighter() {
        IWeighter ahpWeighter = new AHPWeighter();
         Set<WeightResult> results = ahpWeighter.elicitateWeights(comparisonMatricesDirectory, tempWeightsDirectory);

        // Assert
        results.forEach(weightResult -> {
            if (weightResult.name.equals("Total Quality")) {
                Assert.assertEquals(0.9, weightResult.weights.get("QualityAspect 01"), 0.00001);
                Assert.assertEquals(0.1, weightResult.weights.get("QualityAspect 02"), 0.00001);
            }
            else if (weightResult.name.equals("QualityAspect 01")) {
                Assert.assertEquals(0.5, weightResult.weights.get("ProductFactor 01"), 0.00001);
                Assert.assertEquals(0.5, weightResult.weights.get("ProductFactor 02"), 0.00001);
            }
            else if (weightResult.name.equals("QualityAspect 02")) {
                Assert.assertEquals(0.2, weightResult.weights.get("ProductFactor 01"), 0.00001);
                Assert.assertEquals(0.8, weightResult.weights.get("ProductFactor 02"), 0.00001);
            }
        });
    }

}
