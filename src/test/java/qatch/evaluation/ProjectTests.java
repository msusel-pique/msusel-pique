package qatch.evaluation;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import qatch.TestHelper;
import qatch.analysis.Finding;
import qatch.analysis.Measure;
import qatch.model.Property;
import qatch.model.QualityModel;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ProjectTests {

    private Project p;
    private QualityModel qm;
    private double loc;

    @Before
    public void setUp()  {
        this.p = TestHelper.makeProject("TestProject");
        this.loc = this.p.getLinesOfCode();
        this.qm = this.p.getQualityModel();
    }


    @Test
    public void testEvaluateCharacteristics() {

        /*
         * Measure 01, TST0001: 1 finding
         * Measure 01, TST0002: 1 findings
         * Measure 02, TST0003: 1 findings
         * Measure 02, TST0004: 1 findings
         */
        Finding f1 = TestHelper.makeFinding("file/path/f1", 111, 1);

        qm.setFinding(qm.getMeasure("Measure 01"), "TST0001", f1);
        qm.setFinding(qm.getMeasure("Measure 01"), "TST0002", f1);
        qm.setFinding(qm.getMeasure("Measure 02"), "TST0003", f1);
        qm.setFinding(qm.getMeasure("Measure 02"), "TST0004", f1);

        p.evaluateCharacteristics();

        Assert.assertEquals(0.3875, p.getQualityModel().getCharacteristic("Characteristic 01").getValue(loc), 0.001);
        Assert.assertEquals(0.40625, p.getQualityModel().getCharacteristic("Characteristic 02").getValue(loc), 0.001);
    }

    @Test
    public void testEvaluateProperties_negative_lowerGroup() {
        Property p1 = p.getQualityModel().getProperty("Property 01");
        Property p2 = p.getQualityModel().getProperty("Property 02");

        // 0 findings

        p.evaluateProperties();

        Assert.assertEquals(1.0, p1.getValue(loc), 0);
        Assert.assertEquals(1.0, p2.getValue(loc), 0);
    }

    @Test
    public void testEvaluateProperties_negative_middleGroup() {
        Property p1 = p.getQualityModel().getProperty("Property 01");

        /*
         * Measure 01, TST0001: 1 finding
         */
        Finding f1 = TestHelper.makeFinding("file/path/f1", 111, 1);

        qm.setFinding(qm.getMeasure("Measure 01"), "TST0001", f1);

        p.evaluateProperties();

        Assert.assertEquals(0.46875, p1.getValue(loc), .001);
    }

    @Test
    public void testEvaluateProperties_negative_saturation() {
        Property p1 = p.getQualityModel().getProperty("Property 01");

        /*
         * Measure 01, TST0001: 4 findings
         * Measure 01, TST0002: 4 findings
         */
        Finding f1 = TestHelper.makeFinding("file/path/f1", 111, 1);
        Finding f2 = TestHelper.makeFinding("file/path/f2", 222, 1);
        Finding f3 = TestHelper.makeFinding("file/path/f3", 333, 1);
        Finding f4 = TestHelper.makeFinding("file/path/f4", 444, 1);

        qm.setFinding(qm.getMeasure("Measure 01"), "TST0001", f1);
        qm.setFinding(qm.getMeasure("Measure 01"), "TST0001", f2);
        qm.setFinding(qm.getMeasure("Measure 01"), "TST0001", f3);
        qm.setFinding(qm.getMeasure("Measure 01"), "TST0001", f4);

        qm.setFinding(qm.getMeasure("Measure 01"), "TST0002", f1);
        qm.setFinding(qm.getMeasure("Measure 01"), "TST0002", f2);
        qm.setFinding(qm.getMeasure("Measure 01"), "TST0002", f3);
        qm.setFinding(qm.getMeasure("Measure 01"), "TST0002", f4);

        p.evaluateProperties();

        Assert.assertEquals(0.0, p1.getValue(loc), 0);
    }

    @Test
    public void testEvaluateProperties_negative_upperGroup() {

        Property p1 = p.getQualityModel().getProperty("Property 01");

        /*
         * Measure 01, TST0001: 2 findings
         * Measure 01, TST0002: 1 findings
         */
        Finding f1 = TestHelper.makeFinding("file/path/f1", 111, 1);
        Finding f2 = TestHelper.makeFinding("file/path/f2", 222, 1);

        qm.setFinding(qm.getMeasure("Measure 01"), "TST0001", f1);
        qm.setFinding(qm.getMeasure("Measure 01"), "TST0001", f2);

        qm.setFinding(qm.getMeasure("Measure 01"), "TST0002", f1);

        p.evaluateProperties();

        Assert.assertEquals(0.15625, p1.getValue(loc), .000001);
    }

    @Test
    public void testEvaluateProperties_positive_lowerGroup() {
        Property p1 = p.getQualityModel().getProperty("Property 01");
        Property p2 = p.getQualityModel().getProperty("Property 02");

        p1.setPositive(true);
        p2.setPositive(true);
        /*
         * Property 01 measure diagnostic01: 1 finding
         * Property 01 measure diagnostic02: 0 findings
         * Property 02 measure diagnostic01: 0 findings
         * Property 02 measure diagnostic02: 1 findings
         */

        Finding f1 = TestHelper.makeFinding("file/path/f1", 111, 1);

        qm.setFinding(qm.getMeasure("Property 01 measure"), "Property 01 measure diagnostic01", f1);
        qm.setFinding(qm.getMeasure("Property 02 measure"), "Property 02 measure diagnostic01", f1);

        p.evaluateProperties();

        Assert.assertEquals(0, p1.getValue(loc), 0);
        Assert.assertEquals(0, p2.getValue(loc), 0);
    }

    @Test
    public void testEvaluateProperties_positive_middleGroup() {
        Property p1 = p.getQualityModel().getProperty("Property 01");
        Property p2 = p.getQualityModel().getProperty("Property 02");

        p1.setPositive(true);
        p2.setPositive(true);

        /*
         * Property 01 measure diagnostic01: 1 finding
         * Property 01 measure diagnostic02: 1 findings
         * Property 02 measure diagnostic01: 1 findings
         * Property 02 measure diagnostic02: 1 findings
         */
        Finding f1 = TestHelper.makeFinding("file/path/f1", 111, 1);

        qm.setFinding(qm.getMeasure("Property 01 measure"), "Property 01 measure diagnostic01", f1);
        qm.setFinding(qm.getMeasure("Property 01 measure"), "Property 01 measure diagnostic02", f1);
        qm.setFinding(qm.getMeasure("Property 02 measure"), "Property 02 measure diagnostic01", f1);
        qm.setFinding(qm.getMeasure("Property 02 measure"), "Property 02 measure diagnostic02", f1);

        p.evaluateProperties();

        Assert.assertEquals(0.5, p1.getValue(loc), .001);
        Assert.assertEquals(0.5, p2.getValue(loc), .001);
    }

    @Test
    public void testEvaluateProperties_positive_saturation() {
        Property p1 = p.getQualityModel().getProperty("Property 01");
        Property p2 = p.getQualityModel().getProperty("Property 02");

        p1.setPositive(true);
        p2.setPositive(true);
        /*
         * Property 01 measure diagnostic01: 4 finding
         * Property 01 measure diagnostic02: 3 findings
         * Property 02 measure diagnostic01: 3 findings
         * Property 02 measure diagnostic02: 4 findings
         */

        Finding f1 = TestHelper.makeFinding("file/path/f1", 111, 1);
        Finding f2 = TestHelper.makeFinding("file/path/f2", 222, 1);
        Finding f3 = TestHelper.makeFinding("file/path/f3", 333, 1);
        Finding f4 = TestHelper.makeFinding("file/path/f4", 444, 1);

        qm.setFinding(qm.getMeasure("Property 01 measure"), "Property 01 measure diagnostic01", f1);
        qm.setFinding(qm.getMeasure("Property 01 measure"), "Property 01 measure diagnostic01", f2);
        qm.setFinding(qm.getMeasure("Property 01 measure"), "Property 01 measure diagnostic01", f3);
        qm.setFinding(qm.getMeasure("Property 01 measure"), "Property 01 measure diagnostic01", f4);

        qm.setFinding(qm.getMeasure("Property 01 measure"), "Property 01 measure diagnostic02", f1);
        qm.setFinding(qm.getMeasure("Property 01 measure"), "Property 01 measure diagnostic02", f2);
        qm.setFinding(qm.getMeasure("Property 01 measure"), "Property 01 measure diagnostic02", f3);

        qm.setFinding(qm.getMeasure("Property 02 measure"), "Property 02 measure diagnostic01", f1);
        qm.setFinding(qm.getMeasure("Property 02 measure"), "Property 02 measure diagnostic01", f2);
        qm.setFinding(qm.getMeasure("Property 02 measure"), "Property 02 measure diagnostic01", f3);

        qm.setFinding(qm.getMeasure("Property 02 measure"), "Property 02 measure diagnostic02", f1);
        qm.setFinding(qm.getMeasure("Property 02 measure"), "Property 02 measure diagnostic02", f2);
        qm.setFinding(qm.getMeasure("Property 02 measure"), "Property 02 measure diagnostic02", f3);
        qm.setFinding(qm.getMeasure("Property 02 measure"), "Property 02 measure diagnostic02", f4);

        p.evaluateProperties();

        Assert.assertEquals(1.0, p1.getValue(loc), 0);
        Assert.assertEquals(1.0, p2.getValue(loc), 0);
    }

    @Test
    public void testEvaluateProperties_positive_upperGroup() {
        Property p1 = p.getQualityModel().getProperty("Property 01");
        Property p2 = p.getQualityModel().getProperty("Property 02");

        p1.setPositive(true);
        p2.setPositive(true);
        /*
         * Property 01 measure diagnostic01: 2 finding
         * Property 01 measure diagnostic02: 2 findings
         * Property 02 measure diagnostic01: 2 findings
         * Property 02 measure diagnostic02: 2 findings
         */
        Finding f1 = TestHelper.makeFinding("file/path/f1", 111, 1);
        Finding f2 = TestHelper.makeFinding("file/path/f2", 222, 1);

        qm.setFinding(qm.getMeasure("Property 01 measure"), "Property 01 measure diagnostic01", f1);
        qm.setFinding(qm.getMeasure("Property 01 measure"), "Property 01 measure diagnostic01", f2);

        qm.setFinding(qm.getMeasure("Property 01 measure"), "Property 01 measure diagnostic02", f1);
        qm.setFinding(qm.getMeasure("Property 01 measure"), "Property 01 measure diagnostic02", f2);

        qm.setFinding(qm.getMeasure("Property 02 measure"), "Property 02 measure diagnostic01", f1);
        qm.setFinding(qm.getMeasure("Property 02 measure"), "Property 02 measure diagnostic01", f2);

        qm.setFinding(qm.getMeasure("Property 02 measure"), "Property 02 measure diagnostic02", f1);
        qm.setFinding(qm.getMeasure("Property 02 measure"), "Property 02 measure diagnostic02", f2);

        p.evaluateProperties();

        Assert.assertEquals(0.8333, p1.getValue(loc), .001);
        Assert.assertEquals(0.8333, p2.getValue(loc), .001);
    }

    @Test
    public void testEvaluateTqi() {
        /*
         * Property 01 measure diagnostic01: 1 finding
         * Property 01 measure diagnostic02: 1 findings
         * Property 02 measure diagnostic01: 1 findings
         * Property 02 measure diagnostic02: 1 findings
         */
        Finding f1 = TestHelper.makeFinding("file/path/f1", 111, 1);

        qm.setFinding(qm.getMeasure("Property 01 measure"), "Property 01 measure diagnostic01", f1);
        qm.setFinding(qm.getMeasure("Property 01 measure"), "Property 01 measure diagnostic02", f1);
        qm.setFinding(qm.getMeasure("Property 02 measure"), "Property 02 measure diagnostic01", f1);
        qm.setFinding(qm.getMeasure("Property 02 measure"), "Property 02 measure diagnostic02", f1);

        p.evaluateTqi();

        Assert.assertEquals(0.5, p.getQualityModel().getTqi().getValue(loc), 0.001);

    }

    @Test
    public void testExportEvaluation() throws IOException {
        Path exportLocation = Paths.get("src/test/out/TestExportEval");

        /*
         * Property 01 measure diagnostic01: 1 finding
         * Property 01 measure diagnostic02: 1 findings
         * Property 02 measure diagnostic01: 1 findings
         * Property 02 measure diagnostic02: 1 findings
         */
        Finding f1 = TestHelper.makeFinding("file/path/f1", 111, 1);

        qm.setFinding(qm.getMeasure("Property 01 measure"), "Property 01 measure diagnostic01", f1);
        qm.setFinding(qm.getMeasure("Property 01 measure"), "Property 01 measure diagnostic02", f1);
        qm.setFinding(qm.getMeasure("Property 02 measure"), "Property 02 measure diagnostic01", f1);
        qm.setFinding(qm.getMeasure("Property 02 measure"), "Property 02 measure diagnostic02", f1);
        this.p.evaluateTqi();

        Path result = p.exportToJson(exportLocation);
        FileReader fr = new FileReader(result.toString());
        JsonObject jsonResults = new JsonParser().parse(fr).getAsJsonObject();
        fr.close();

        int loc = jsonResults.getAsJsonPrimitive("linesOfCode").getAsInt();
        String name = jsonResults.getAsJsonPrimitive("name").getAsString();
        JsonObject jsonTqi = jsonResults.getAsJsonObject("qualityModel").getAsJsonObject("tqi");
        double tqiValue = jsonTqi.getAsJsonPrimitive("value").getAsDouble();
        int numCharacteristics = jsonTqi.getAsJsonObject("characteristics").keySet().size();
        int numProperties = jsonTqi.getAsJsonObject("characteristics").getAsJsonObject("Characteristic 01").getAsJsonObject("properties").keySet().size();

        Assert.assertEquals(10, loc);
        Assert.assertEquals("TestProject", name);
        Assert.assertEquals(2, numCharacteristics);
        Assert.assertEquals(2, numProperties);
        Assert.assertEquals(0.5, tqiValue, 0);

        FileUtils.forceDelete(exportLocation.toFile());
    }

    /*
     * Test state of fields of project object immediately after construction
     */
    @Test
    public void testProjectConstructor() {
        String name = "Test Project";
        Path path = Paths.get("test/path");
        QualityModel qm = TestHelper.makeQualityModel();

        Project project = new Project(name, path, qm);

        // Assertions
        Assert.assertEquals(name, project.getName());
        Assert.assertEquals(path, project.getPath());

        // Assert tree structure pass-by-reference from TQI root node compared to fields
        project.getQualityModel().getTqi().getCharacteristics().values().forEach(tqiCharacteristic -> {
            // Characteristic layer
            Assert.assertEquals(project.getQualityModel().getCharacteristics().get(tqiCharacteristic.getName()), tqiCharacteristic);
            tqiCharacteristic.getProperties().values().forEach(tqiProperty -> {
                // Properties layer
                Assert.assertEquals(project.getQualityModel().getProperties().get(tqiProperty.getName()), tqiProperty);
                Measure projectMeasure = project.getQualityModel().getProperties().get(tqiProperty.getName()).getMeasure();
                // Property's measure
                Assert.assertEquals(projectMeasure, tqiProperty.getMeasure());
                tqiProperty.getMeasure().getDiagnostics().forEach(tqiDiagnostic -> {
                    // Measure's diagnostics
                    Assert.assertEquals(projectMeasure.getDiagnostic(tqiDiagnostic.getName()), tqiDiagnostic);
                });
            });
        });

    }
}
