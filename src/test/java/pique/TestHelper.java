//package pique;
//
//import org.apache.commons.io.FileUtils;
//import pique.analysis.*;
//import pique.evaluation.DefaultDiagnosticEvaluator;
//import pique.evaluation.LoCEvaluator;
//import pique.evaluation.LoCNormalizer;
//import pique.evaluation.Project;
//import pique.model.*;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Utility framework for quickly generating model, project, and tool objects for testing
// */
//public class TestHelper {
//
//    public static final Path TEST_DIR = new File("src/test").toPath();
//    public static final Path TEST_RESOURCES = Paths.get(TEST_DIR.toString(), "resources").toAbsolutePath();
//    public static final Path OUTPUT = Paths.get(TEST_DIR.toString(), "out").toAbsolutePath();
//    private static final Path QUALITY_MODEL = Paths.get(TEST_RESOURCES.toString(), "quality_models", "qualityModel_test.json");
//    private static final Path QUALITY_MODEL_DESC = Paths.get(TEST_RESOURCES.toString(), "quality_models", "qualityModel_test_description.json");
//
//    /*
//     * Analysis objects
//     */
//    public static Diagnostic makeDiagnostic(String id) {
//        return new Diagnostic(id, "Sample Description", "Sample Tool Name", new DefaultDiagnosticEvaluator());
//    }
//
//    public static Finding makeFinding(String filePath, int lineNumber, int severity) {
//        return new Finding(filePath, lineNumber, 11, severity);
//    }
//
//    public static Measure makeMeasure(String name) {
//        Diagnostic d1 = makeDiagnostic(name + " diagnostic01");
//        Diagnostic d2 = makeDiagnostic(name + " diagnostic02");
//        return new Measure(name, name + " tool name", new LoCNormalizer(), false, Arrays.asList(d1, d2));
//    }
//
//
//    /*
//     * Evaluation objects
//     */
//    /**
//     * Make project without reliance on a quality model file
//     */
//    public static Project makeProject(String name) {
//        Project project = new Project(name);
//        project.setLinesOfCode(200);
//        project.setQualityModel(makeQualityModel());
//        return project;
//    }
//
//    public static QualityModel makeQualityModel() {
//        return new QualityModel(QUALITY_MODEL);
//    }
//
//    /*
//     * Model objects
//     */
//    public static QualityAspect makeCharacteristic(String name) {
//        HashMap<String, Double> weights = new HashMap<String, Double>() {{
//            put("ProductFactor 01", 0.6);
//            put("ProductFactor 02", 0.4);
//        }};
//        return new QualityAspect(name, name + " description", weights);
//    }
//    /**
//     * Automatically create a measure with diagnostics and finidings and attach to property field
//     * @param name
//     *      ProductFactor name
//     * @return
//     *      ProductFactor with satisfying data in its fields
//     */
//    public static ProductFactor makeProperty(String name) {
//        Measure m = makeMeasure(name + " measure");
//        ProductFactor p = new ProductFactor(name, "Sample Description", m);
//        p.getMeasure().setThresholds(new Double[] {0.1, 0.2, 0.5});
//        return p;
//    }
//
//    public static Tqi makeTqi(String name) {
//        HashMap<String, Double> weights = new HashMap<String, Double>() {{
//            put("QualityAspect 01", 0.7);
//            put("QualityAspect 02", 0.3);
//        }};
//        return new Tqi(name, "Tqi description from TestHelper.", weights);
//    }
//
//    /*
//     * Tool objects
//     */
//    public static ITool makeITool() {
//       return new ITool() {
//            @Override
//            public Path analyze(Path projectLocation) {
//                return Paths.get("src/test/resources/tool_results/faketool_output.xml");
//            }
//
//           @Override
//            public Map<String, Diagnostic> parseAnalysis(Path toolResults) {
//                Map<String, Diagnostic> diagnostics = new HashMap<>();
//
//                Finding f1 = TestHelper.makeFinding("file/path/f1", 111, 1);
//
//                /*
//                 * Have enough findings to hit middle group of threshold property evaluation
//                 *
//                 * Measure 01, TST0001: 1 finding
//                 * Measure 02, TST0003: 1 finding
//                 */
//                Diagnostic tst01 = TestHelper.makeDiagnostic("TST0001");
//                Diagnostic tst03 = TestHelper.makeDiagnostic("TST0003");
//
//                tst01.setFinding(f1);
//                tst03.setFinding(f1);
//
//                diagnostics.put("TST0001", tst01);
//                diagnostics.put("TST0003", tst03);
//
//                return diagnostics;
//            }
//
//           @Override
//           public Path initialize(Path toolRoot) {
//               return null;
//           }
//
//           @Override
//            public String getName() {
//                return "Fake Tool";
//            }
//        };
//    }
//
//    public static ITool makeIToolLoc() {
//        return new ITool() {
//            @Override
//            public Path analyze(Path projectLocation) {
//                return Paths.get("src/test/resources/tool_results/faketool_loc_output.txt");
//            }
//
//            @Override
//            public Map<String, Diagnostic> parseAnalysis(Path toolResults) {
//
//                Map<String, Diagnostic> diagnostics = new HashMap<>();
//
//                Finding f1 = new Finding(200);
//                Diagnostic locDiagnostic = new Diagnostic("loc", "loc diagnostic description", this.getName(), new LoCEvaluator());
//
//                locDiagnostic.setFinding(f1);
//                diagnostics.put(locDiagnostic.getName(), locDiagnostic);
//
//                return diagnostics;
//            }
//
//            @Override
//            public Path initialize(Path toolRoot) {
//                return null;
//            }
//
//            @Override
//            public String getName() {
//                return "Fake LoC Tool";
//            }
//        };
//    }
//
//    /*
//     * Other
//     */
//    public static void clean(File dest) throws IOException {
//        if (dest.exists()) { FileUtils.cleanDirectory(dest); }
//        else dest.mkdirs();
//    }
//
//    public static void cleanTestOutput() throws IOException {
//        FileUtils.deleteDirectory(OUTPUT.toFile());
//    }
//}
