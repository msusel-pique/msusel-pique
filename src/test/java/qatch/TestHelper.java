package qatch;

import org.apache.commons.io.FileUtils;
import qatch.analysis.*;
import qatch.evaluation.Project;
import qatch.model.Characteristic;
import qatch.model.Property;
import qatch.model.QualityModel;
import qatch.model.Tqi;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility framework for quickly generating model, project, and tool objects for testing
 */
public class TestHelper {

    public static final Path TEST_DIR = new File("src/test").toPath();
    public static final Path TEST_RESOURCES = Paths.get(TEST_DIR.toString(), "resources").toAbsolutePath();
    public static final Path OUTPUT = Paths.get(TEST_DIR.toString(), "out").toAbsolutePath();
    private static final Path QUALITY_MODEL = Paths.get(TEST_RESOURCES.toString(), "quality_models", "qualityModel_test.json");
    private static final Path QUALITY_MODEL_DESC = Paths.get(TEST_RESOURCES.toString(), "quality_models", "qualityModel_test_description.json");

    /*
     * Analysis objects
     */
    public static Diagnostic makeDiagnostic(String id) {
        return new Diagnostic(id, "Sample Description", "Sample Tool Name");
    }

    public static Finding makeFinding(String filePath, int lineNumber, int severity) {
        return new Finding(filePath, lineNumber, 11, severity);
    }

    public static Measure makeMeasure(String name) {
        Diagnostic d1 = makeDiagnostic(name + " diagnostic01");
        Diagnostic d2 = makeDiagnostic(name + " diagnostic02");
        return new Measure(name, name + " tool name", Arrays.asList(d1, d2));
    }


    /*
     * Evaluation objects
     */
    /**
     * Make project without reliance on a quality model file
     */
    public static Project makeProject(String name) {
        Project project = new Project(name);
        project.setLinesOfCode(200);
        project.setQualityModel(makeQualityModel());
        return project;
    }

    public static QualityModel makeQualityModel() {
        return new QualityModel(QUALITY_MODEL);
    }

    /*
     * Model objects
     */
    public static Characteristic makeCharacteristic(String name) {
        HashMap<String, Double> weights = new HashMap<String, Double>() {{
            put("Property 01", 0.6);
            put("Property 02", 0.4);
        }};
        return new Characteristic(name, name + " description", weights);
    }
    /**
     * Automatically create a measure with diagnostics and finidings and attach to property field
     * @param name
     *      Property name
     * @return
     *      Property with satisfying data in its fields
     */
    public static Property makeProperty(String name) {
        Measure m = makeMeasure(name + " measure");
        Property p = new Property(name, "Sample Description", m);
        p.setThresholds(new Double[] {0.1, 0.2, 0.5});
        return p;
    }

    public static Tqi makeTqi(String name) {
        HashMap<String, Double> weights = new HashMap<String, Double>() {{
            put("Characteristic 01", 0.7);
            put("Characteristic 02", 0.3);
        }};
        return new Tqi(name, "Tqi description from TestHelper.", weights);
    }

    /*
     * Tool objects
     */
    public static ITool makeITool() {
       return new ITool() {
            @Override
            public Path analyze(Path projectLocation) {
                return Paths.get("src/test/resources/tool_results/faketool_output.xml");
            }

            @Override
            public Map<String, Diagnostic> parseAnalysis(Path toolResults) {
                Map<String, Diagnostic> diagnostics = new HashMap<>();

                Finding f1 = TestHelper.makeFinding("file/path/f1", 111, 1);

                Diagnostic tst01 = TestHelper.makeDiagnostic("TST0001");
                Diagnostic tst02 = TestHelper.makeDiagnostic("TST0002");
                Diagnostic tst03 = TestHelper.makeDiagnostic("TST0003");
                Diagnostic tst04 = TestHelper.makeDiagnostic("TST0004");
                Diagnostic tst05 = TestHelper.makeDiagnostic("TST0005");

                tst01.setFinding(f1);
                tst02.setFinding(f1);
                tst04.setFinding(f1);
                tst05.setFinding(f1);

                diagnostics.put("TST0001", tst01);
                diagnostics.put("TST0002", tst02);
                diagnostics.put("TST0003", tst03);
                diagnostics.put("TST0004", tst04);
                diagnostics.put("TST0005", tst05);

                return diagnostics;
            }

            @Override
            public String getName() {
                return "Fake Tool";
            }
        };
    }

    public static IToolLOC makeIToolLoc() {
        return new IToolLOC() {
            @Override
            public Integer analyze(Path projectLocation) {
                return 200;
            }
        };
    }

    /*
     * Other
     */
    public static void clean(File dest) throws IOException {
        if (dest.exists()) { FileUtils.cleanDirectory(dest); }
        else dest.mkdirs();
    }

    public static void cleanTestOutput() throws IOException {
        FileUtils.deleteDirectory(OUTPUT.toFile());
    }
}
