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
        project.setLinesOfCode(100);
        project.setQualityModel(makeQualityModel());
        return project;
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

    /**
     * Make quality model without reliance on a hard-drive file
     * @return
     *      QualityModel with valid instances in all fields
     */
    public static QualityModel makeQualityModel() {
        QualityModel qm = new QualityModel("Test Quality Model");
        Characteristic c1 = makeCharacteristic("Characteristic 01");
        Characteristic c2 = makeCharacteristic("Characteristic 02");
        Property p1 = makeProperty("Property 01");
        Property p2 = makeProperty("Property 02");
        Map<String, Characteristic> characteristics = new HashMap<String, Characteristic>() {{
            put(c1.getName(), c1);
            put(c2.getName(), c2);
        }};
        Map<String, Property> properties = new HashMap<String, Property>() {{
            put(p1.getName(), p1);
            put(p2.getName(), p2);
        }};
        Tqi tqi = TestHelper.makeTqi("Test TQI");

        qm.setTqi(tqi);
        qm.setCharacteristics(characteristics);
        qm.setProperties(properties);

        return qm;
    }

    public static Tqi makeTqi(String name) {
        HashMap<String, Double> weights = new HashMap<String, Double>() {{
            put("Characteristic 01", 0.7);
            put("Characteristic 02", 0.3);
        }};
        Tqi tqi = new Tqi(name, "Tqi description from TestHelper.", weights);
        return tqi;
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
                diagnostics.put("TST0001", TestHelper.makeDiagnostic("TST0001"));
                diagnostics.put("TST0002", TestHelper.makeDiagnostic("TST0002"));
                diagnostics.put("TST0003", TestHelper.makeDiagnostic("TST0003"));
                diagnostics.put("TST0004", TestHelper.makeDiagnostic("TST0004"));
                diagnostics.put("TST0005", TestHelper.makeDiagnostic("TST0005"));
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
                return 1000;
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
