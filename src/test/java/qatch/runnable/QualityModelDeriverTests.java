package qatch.runnable;

import org.junit.Assert;
import org.junit.Test;
import qatch.TestHelper;
import qatch.analysis.Diagnostic;
import qatch.analysis.Finding;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        IToolLOC fakeLocTool = projectLocation -> 50;
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
             * Test info for assertion reference:
             *
             * Project 1:
             *      Property 01, Measure 01, Diagnostic TST01: 1 finding
             *      Property 02, Measure 02, Diagnostic TST03: 1 finidng
             * Project 2:
             *      Property 01, Measure 01, Diagnostic TST01: 2 findings
             *      Property 01, Measure 01, Diagnostic TST02: 2 findings
             *      Property 02, Measure 02, Diagnostic TST03: 2 findings
             *      Property 02, Measure 02, Diagnostic TST04: 2 findings
             *      Property 02, Measure 02, Diagnostic TST05: 2 findings
             * Project 3:
             *      Property 01, Measure 01, Diagnostic TST01: 3 findings
             *      Property 01, Measure 01, Diagnostic TST02: 3 findings
             *      Property 02, Measure 02, Diagnostic TST03: 3 findings
             *      Property 02, Measure 02, Diagnostic TST04: 3 findings
             *      Property 02, Measure 02, Diagnostic TST05: 3 findings
             */
            @Override
            public Map<String, Diagnostic> parseAnalysis(Path toolResults) {
                Finding f1 = TestHelper.makeFinding("file/path/one", 11, 1);
                Finding f2 = TestHelper.makeFinding("file/path/two", 22, 2);
                Finding f3 = TestHelper.makeFinding("file/path/three", 33, 3);

                Map<String, Diagnostic> diagnostics = new HashMap<>();
                switch (toolResults.getFileName().toString()) {
                    case "benchmark_one_results.xml":

                        Diagnostic bench1tst1 = new Diagnostic("TST0001", "Sample Description", "Sample Tool Name");
                        Diagnostic bench1tst3 = new Diagnostic("TST0003", "Sample Description", "Sample Tool Name");
                        bench1tst1.setFinding(f1);
                        bench1tst3.setFinding(f1);

                        diagnostics.put("TST0001", bench1tst1);
                        diagnostics.put("TST0003", bench1tst3);

                        return diagnostics;

                    case "benchmark_two_results.xml":
                        Diagnostic bench2tst1 = new Diagnostic("TST0001", "Sample Description", "Sample Tool Name");
                        Diagnostic bench2tst2 = new Diagnostic("TST0002", "Sample Description", "Sample Tool Name");
                        Diagnostic bench2tst3 = new Diagnostic("TST0003", "Sample Description", "Sample Tool Name");
                        Diagnostic bench2tst4 = new Diagnostic("TST0004", "Sample Description", "Sample Tool Name");
                        Diagnostic bench2tst5 = new Diagnostic("TST0005", "Sample Description", "Sample Tool Name");

                        bench2tst1.setFindings(Stream.of(f1, f2).collect(Collectors.toSet()));
                        bench2tst2.setFindings(Stream.of(f1, f2).collect(Collectors.toSet()));
                        bench2tst3.setFindings(Stream.of(f1, f2).collect(Collectors.toSet()));
                        bench2tst4.setFindings(Stream.of(f1, f2).collect(Collectors.toSet()));
                        bench2tst5.setFindings(Stream.of(f1, f2).collect(Collectors.toSet()));

                        diagnostics.put("TST0001", bench2tst1);
                        diagnostics.put("TST0002", bench2tst2);
                        diagnostics.put("TST0003", bench2tst3);
                        diagnostics.put("TST0004", bench2tst4);
                        diagnostics.put("TST0005", bench2tst5);

                        return diagnostics;

                    case "benchmark_three_results.xml":
                        Diagnostic bench3tst1 = new Diagnostic("TST0001", "Sample Description", "Sample Tool Name");
                        Diagnostic bench3tst2 = new Diagnostic("TST0002", "Sample Description", "Sample Tool Name");
                        Diagnostic bench3tst3 = new Diagnostic("TST0003", "Sample Description", "Sample Tool Name");
                        Diagnostic bench3tst4 = new Diagnostic("TST0004", "Sample Description", "Sample Tool Name");
                        Diagnostic bench3tst5 = new Diagnostic("TST0005", "Sample Description", "Sample Tool Name");

                        bench3tst1.setFindings(Stream.of(f1, f2, f3).collect(Collectors.toSet()));
                        bench3tst2.setFindings(Stream.of(f1, f2, f3).collect(Collectors.toSet()));
                        bench3tst3.setFindings(Stream.of(f1, f2, f3).collect(Collectors.toSet()));
                        bench3tst4.setFindings(Stream.of(f1, f2, f3).collect(Collectors.toSet()));
                        bench3tst5.setFindings(Stream.of(f1, f2, f3).collect(Collectors.toSet()));

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
            public String getName() {
                return "Deriver Test Tool";
            }
        };

        Map<String, ITool> tools = new HashMap<String, ITool>() {{ put(tool.getName(), tool); }};

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

        Assert.assertArrayEquals(new Double[]{0.02, 0.08, 0.12}, p1.getThresholds());
        Assert.assertArrayEquals(new Double[]{0.02, 0.12, 0.18}, p2.getThresholds());
    }
}
