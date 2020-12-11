//package pique.runnable;
//
//import org.junit.Test;
//import pique.TestHelper;
//import pique.analysis.ITool;
//import pique.evaluation.DefaultDiagnosticEvaluator;
//import pique.evaluation.LoCEvaluator;
//import pique.model.*;
//import sun.reflect.generics.reflectiveObjects.NotImplementedException;
//
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Set;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
///**
// * "Integration tests" of model derivation procedures.
// */
//public class QualityModelDeriverTests {
//
//    //region Constructs
//    String projectRootFlag = ".txt";
//
//    Path benchmarkRepo = Paths.get("src/test/resources/benchmark_repository");
//    Path qmDescriptionPath = Paths.get("src/test/resources/quality_models/qualityModel_test_description.json");
//
//    //region Mocked Static Analysis Tools
//
//    // LoC diagnostic tool which returns 50 lines of code
//    ITool fakeLocTool = new ITool() {
//        @Override
//        public Path analyze(Path projectLocation) {
//            return Paths.get("src/test/resources/tool_results/faketool_loc_output.txt");
//        }
//
//        @Override
//        public Map<String, Diagnostic> parseAnalysis(Path toolResults) {
//            Map<String, Diagnostic> diagnostics = new HashMap<>();
//            Finding f1 = new Finding(50);
//            Diagnostic locDiagnostic = new Diagnostic("loc", "loc diagnostic description", this.getName(), new LoCEvaluator());
//            locDiagnostic.setFinding(f1);
//            diagnostics.put(locDiagnostic.getName(), locDiagnostic);
//            return diagnostics;
//        }
//
//        @Override
//        public Path initialize(Path toolRoot) {
//            return null;
//        }
//
//        @Override
//        public String getName() {
//            return "Fake LoC Tool";
//        }
//    };
//
//    // ITool which returns different values for each project
//    ITool tool = new ITool() {
//        @Override
//        public Path analyze(Path projectLocation) {
//            switch (projectLocation.getFileName().toString()) {
//                case "BenchmarkProjectOne":
//                    return Paths.get("src/test/resources/tool_results/benchmark_one_results.xml");
//                case "BenchmarkProjectTwo":
//                    return Paths.get("src/test/resources/tool_results/benchmark_two_results.xml");
//                case "BenchmarkProjectThree":
//                    return Paths.get("src/test/resources/tool_results/benchmark_three_results.xml");
//                default:
//                    throw new RuntimeException("switch statement default case");
//            }
//        }
//
//        /*
//         * Test info for reference and assertions:
//         *
//         * Project 01:
//         *      Measure 01, TST0001: 1 finding
//         *      Measure 02, TST0003: 1 finding
//         * Project 02:
//         *      Measure 01, TST0001: 1 finding
//         *      Measure 01, TST0002: 1 finding
//         *      Measure 02, TST0003: 1 finding
//         *      Measure 02, TST0004: 1 finding
//         *      Measure 02, TST0005: 1 finding
//         * Project 03:
//         *      Measure 01, TST0001: 2 findings
//         *      Measure 01, TST0002: 2 findings
//         *      Measure 02, TST0003: 2 findings
//         *      Measure 02, TST0004: 2 findings
//         *      Measure 02, TST0005: 2 findings
//         */
//        @Override
//        public Map<String, Diagnostic> parseAnalysis(Path toolResults) {
//            Finding f1 = TestHelper.makeFinding("file/path/one", 11, 1);
//            Finding f2 = TestHelper.makeFinding("file/path/two", 22, 2);
//
//            Map<String, Diagnostic> diagnostics = new HashMap<>();
//            switch (toolResults.getFileName().toString()) {
//                case "benchmark_one_results.xml":
//
//                    Diagnostic bench1tst1 = new Diagnostic("TST0001", "Sample Description", "Sample Tool Name", new DefaultDiagnosticEvaluator());
//                    Diagnostic bench1tst3 = new Diagnostic("TST0003", "Sample Description", "Sample Tool Name", new DefaultDiagnosticEvaluator());
//                    bench1tst1.setFinding(f1);
//                    bench1tst3.setFinding(f1);
//
//                    diagnostics.put("TST0001", bench1tst1);
//                    diagnostics.put("TST0003", bench1tst3);
//
//                    return diagnostics;
//
//                case "benchmark_two_results.xml":
//                    Diagnostic bench2tst1 = new Diagnostic("TST0001", "Sample Description", "Sample Tool Name", new DefaultDiagnosticEvaluator());
//                    Diagnostic bench2tst2 = new Diagnostic("TST0002", "Sample Description", "Sample Tool Name", new DefaultDiagnosticEvaluator());
//                    Diagnostic bench2tst3 = new Diagnostic("TST0003", "Sample Description", "Sample Tool Name", new DefaultDiagnosticEvaluator());
//                    Diagnostic bench2tst4 = new Diagnostic("TST0004", "Sample Description", "Sample Tool Name", new DefaultDiagnosticEvaluator());
//                    Diagnostic bench2tst5 = new Diagnostic("TST0005", "Sample Description", "Sample Tool Name", new DefaultDiagnosticEvaluator());
//
//                    bench2tst1.setFinding(f1);
//                    bench2tst2.setFinding(f1);
//                    bench2tst3.setFinding(f1);
//                    bench2tst4.setFinding(f1);
//                    bench2tst5.setFinding(f1);
//
//                    diagnostics.put("TST0001", bench2tst1);
//                    diagnostics.put("TST0002", bench2tst2);
//                    diagnostics.put("TST0003", bench2tst3);
//                    diagnostics.put("TST0004", bench2tst4);
//                    diagnostics.put("TST0005", bench2tst5);
//
//                    return diagnostics;
//
//                case "benchmark_three_results.xml":
//                    Diagnostic bench3tst1 = new Diagnostic("TST0001", "Sample Description", "Sample Tool Name", new DefaultDiagnosticEvaluator());
//                    Diagnostic bench3tst2 = new Diagnostic("TST0002", "Sample Description", "Sample Tool Name", new DefaultDiagnosticEvaluator());
//                    Diagnostic bench3tst3 = new Diagnostic("TST0003", "Sample Description", "Sample Tool Name", new DefaultDiagnosticEvaluator());
//                    Diagnostic bench3tst4 = new Diagnostic("TST0004", "Sample Description", "Sample Tool Name", new DefaultDiagnosticEvaluator());
//                    Diagnostic bench3tst5 = new Diagnostic("TST0005", "Sample Description", "Sample Tool Name", new DefaultDiagnosticEvaluator());
//
//                    bench3tst1.setFindings(Stream.of(f1, f2).collect(Collectors.toSet()));
//                    bench3tst2.setFindings(Stream.of(f1, f2).collect(Collectors.toSet()));
//                    bench3tst3.setFindings(Stream.of(f1, f2).collect(Collectors.toSet()));
//                    bench3tst4.setFindings(Stream.of(f1, f2).collect(Collectors.toSet()));
//                    bench3tst5.setFindings(Stream.of(f1, f2).collect(Collectors.toSet()));
//
//                    diagnostics.put("TST0001", bench3tst1);
//                    diagnostics.put("TST0002", bench3tst2);
//                    diagnostics.put("TST0003", bench3tst3);
//                    diagnostics.put("TST0004", bench3tst4);
//                    diagnostics.put("TST0005", bench3tst5);
//
//                    return diagnostics;
//
//                default:
//                    throw new RuntimeException("switch statement default case");
//            }
//        }
//
//        @Override
//        public Path initialize(Path toolRoot) {
//            return null;
//        }
//
//        @Override
//        public String getName() {
//            return "Deriver Test Tool";
//        }
//    };
//
//    //endregion
//
//    Set<ITool> tools = Stream.of(tool, fakeLocTool).collect(Collectors.toSet());
//
//    //endregion
//
//    //region Tests
//
//    @Test
//    public void testDeriveModel() {
//
//        // Initialize objects
//        QualityModel qmDescription = new QualityModel(qmDescriptionPath);
//
//        // Run process
//        QualityModel qm = QualityModelDeriver.deriveModel(qmDescription, tools, benchmarkRepo,
//                projectRootFlag);
//
//        // Assert
//        Tqi tqi = qm.getTqi();
//        QualityAspect c1 = qm.getQualityAspect("QualityAspect 01");
//        QualityAspect c2 = qm.getQualityAspect("QualityAspect 02");
//        ProductFactor p1 = qm.getProductFactor("ProductFactor 01");
//        ProductFactor p2 = qm.getProductFactor("ProductFactor 02");
//
//        throw new NotImplementedException();
//
////        Assert.assertEquals(0.5, tqi.getWeight("QualityAspect 01"), 0.0001);
//    }
//
//    //endregion
//
//
//}
