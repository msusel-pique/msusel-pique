package qatch;

import org.apache.commons.io.FileUtils;
import qatch.analysis.Diagnostic;
import qatch.analysis.Finding;
import qatch.analysis.Measure;
import qatch.evaluation.Project;
import qatch.model.Characteristic;
import qatch.model.Property;
import qatch.model.Tqi;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Test utility class for creating commonly used Qatch model objects and references
 */
public class TestHelper {

    public static final Path TEST_DIR = new File("src/test").toPath();
    public static final Path TEST_RESOURCES = Paths.get(TEST_DIR.toString(), "resources").toAbsolutePath();
    public static final Path OUTPUT = Paths.get(TEST_DIR.toString(), "output").toAbsolutePath();


    public static Characteristic makeCharacteristic(String name) {
        HashMap<String, Double> weights = new HashMap<String, Double>() {{
            put("Property 01", 0.6);
            put("Property 02", 0.4);
        }};
        return new Characteristic(name, name + " standard", name + " description", weights);
    }

    public static Diagnostic makeDiagnostic(String id) {
        Finding f1 = makeFinding(id + "/filepath/f1", 234, 2);
        Finding f2 = makeFinding(id + "/filepath/f2", 345, 3);
        Diagnostic d = new Diagnostic(id);
        d.setFinding(f1);
        d.setFinding(f2);
        return d;
    }

    public static Finding makeFinding(String filePath, int lineNumber, int severity) {
        return new Finding(filePath, lineNumber, 11, severity);
    }

    public static Measure makeMeasure(String name) {
        Diagnostic d1 = makeDiagnostic(name + " diagnostic01");
        Diagnostic d2 = makeDiagnostic(name + " diagnostic02");
        return new Measure(name, name + " tool name", Arrays.asList(d1, d2));
    }

    public static Property makeProperty(String name) {
        Measure m = makeMeasure(name + " measure");
        Property p = new Property(name, m);
        p.setThresholds(new double[] {0.0, 0.5, 1.0});
        return p;
    }

    public static Tqi makeTqi(String name) {
        HashMap<String, Double> weights = new HashMap<String, Double>() {{
            put("Characteristic 01", 0.7);
            put("Characteristic 02", 0.3);
        }};
        return new Tqi(name, weights);
    }

    public static Project makeProject(String name) {
        Property p1 = makeProperty("Property 01");
        Property p2 = makeProperty("Property 02");
        Characteristic c1 = makeCharacteristic("Characteristic 01");
        Characteristic c2 = makeCharacteristic("Characteristic 02");
        Tqi tqi = makeTqi("TQI");

        Project project = new Project(name);
        project.setLinesOfCode(555);
        project.setTqi(tqi);
        project.getTqi().setValue(0.92);
        project.setCharacteristic(c1.getName(), c1);
        project.setCharacteristic(c2.getName(), c2);
        project.setProperty(p1.getName(), p1);
        project.setProperty(p2.getName(), p2);

        return project;
    }

    public static void clean(File dest) throws IOException {
        if (dest.exists()) { FileUtils.cleanDirectory(dest); }
        else dest.mkdirs();
    }

    public static void cleanTestOutput() throws IOException {
        FileUtils.deleteDirectory(OUTPUT.toFile());
    }
}
