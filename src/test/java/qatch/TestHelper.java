package qatch;

import org.apache.commons.io.FileUtils;
import qatch.analysis.Measure;
import qatch.evaluation.Project;
import qatch.model.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Vector;

/**
 * Test utility class for creating commonly used Qatch model objects and references
 */
public class TestHelper {

    public static final Path TEST_DIR = new File("src/test").toPath();
    public static final Path TEST_RESOURCES = Paths.get(TEST_DIR.toString(), "resources").toAbsolutePath();
    public static final Path OUTPUT = Paths.get(TEST_DIR.toString(), "output").toAbsolutePath();

    public static Issue makeIssue(String ruleName) {
        Issue i = new Issue();
        i.setRuleName(ruleName);
        return i;
    }

    public static Metrics makeMetric(String name, int loc) {
        Metrics m = new Metrics();
        m.setName(name);
        m.setLoc(loc);
        return m;
    }

    public static Measure makeMeasureMetric(String name) {
        return new Measure(Measure.METRIC, name, "./ruleset/path/" + name + ".xml", name + " Tool");
    }

    public static Measure makeMeasureFinding(String name) {
        return new Measure(Measure.FINDING, name, "./ruleset/path/" + name + ".xml", name + " Tool");
    }

    public static Property makeProperty(String name, Measure measure) {
        Property p = new Property(name, measure);
        p.setThresholds(new double[] {0.0, 0.5, 1.0});
        return p;
    }

    public static Characteristic makeCharacteristic(String name) {
        return new Characteristic(name, name + " standard", name + " description", new Vector<Double>(Arrays.asList(0.3, 0.7)));
    }

    public static Tqi makeTqi() {
        return new Tqi(new Vector<Double>(Arrays.asList(0.3, 0.7)));
    }

    public static IssueSet makeIssueSet(String name, Issue... issues) {
        IssueSet is = new IssueSet(name);
        Arrays.stream(issues).forEach(is::addIssue);
        return is;
    }

    public static MetricSet makeMetricSet(Metrics... metrics) {
        MetricSet ms = new MetricSet();
        Arrays.stream(metrics).forEach(ms::addMetrics);
        return ms;
    }

    public static PropertySet makePropertySet(Property... properties) {
        PropertySet ps = new PropertySet();
        Arrays.stream(properties).forEach(ps::addProperty);
        return ps;
    }

    public static CharacteristicSet makeCharacteristicSet(Characteristic... characteristics) {
        CharacteristicSet cs = new CharacteristicSet();
        Arrays.stream(characteristics).forEach(cs::addCharacteristic);
        return cs;
    }

    public static Project makeProject(String name) {
        Issue i1 = makeIssue("Rule 01");
        Issue i2 = makeIssue("Rule 02");
        Issue i3 = makeIssue("Rule 03");
        IssueSet is1 = makeIssueSet("Issue Set 01", i1, i2);
        IssueSet is2 = makeIssueSet("Issue Set 02", i3);
        Metrics met1 = makeMetric("Metric 01", 111);
        Metrics met2 = makeMetric("Metric 02", 222);
        Measure meas1 = makeMeasureFinding("Measure Finding 01");
        Measure meas2 = makeMeasureMetric("Measure Metric 01");
        Property p1 = makeProperty("Property 01", meas1);
        Property p2 = makeProperty("Property 02", meas2);
        Characteristic c1 = makeCharacteristic("Characteristic 01");
        Characteristic c2 = makeCharacteristic("Characteristic 02");
        CharacteristicSet cset1 = makeCharacteristicSet(c1, c2);
        Tqi tqi = makeTqi();

        Project project = new Project(name);
        project.addIssueSet(is1);
        project.addIssueSet(is2);
        project.addMetrics(met1);
        project.addMetrics(met2);
        project.addProperty(p1);
        project.addProperty(p2);
        project.setCharacteristics(cset1);
        project.setTqi(tqi);

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
