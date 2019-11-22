package qatch.evaluation;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import qatch.TestHelper;
import qatch.analysis.Measure;
import qatch.model.Property;
import qatch.model.QualityModel;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ProjectTests {

    private Project p;

    @Before
    public void setUp()  {
        this.p = TestHelper.makeProject("TestProject");
    }


    @Test
    public void testEvaluateCharacteristics() {
        p.getQualityModel().getProperty("Property 01").setValue(0.1);
        p.getQualityModel().getProperty("Property 02").setValue(0.9);
        p.evaluateCharacteristics();

        Assert.assertEquals(0.42, p.getQualityModel().getCharacteristic("Characteristic 01").getValue(), 0.001);
        Assert.assertEquals(0.42, p.getQualityModel().getCharacteristic("Characteristic 02").getValue(), 0.001);
    }

    @Test
    public void testEvaluateProperties_negative_lowerGroup() {
        Property p1 = p.getQualityModel().getProperty("Property 01");
        Property p2 = p.getQualityModel().getProperty("Property 02");

        p1.getMeasure().setNormalizedValue(0.01);
        p2.getMeasure().setNormalizedValue(0.09);

        p.evaluateProperties();

        Assert.assertEquals(1.0, p1.getValue(), 0);
        Assert.assertEquals(1.0, p2.getValue(), 0);
    }

    @Test
    public void testEvaluateProperties_negative_middleGroup() {
        Property p1 = p.getQualityModel().getProperty("Property 01");
        Property p2 = p.getQualityModel().getProperty("Property 02");

        p1.getMeasure().setNormalizedValue(0.11);
        p2.getMeasure().setNormalizedValue(0.19);

        p.evaluateProperties();

        Assert.assertEquals(0.95, p1.getValue(), .001);
        Assert.assertEquals(0.55, p2.getValue(), .001);
    }

    @Test
    public void testEvaluateProperties_negative_saturation() {
        Property p1 = p.getQualityModel().getProperty("Property 01");
        Property p2 = p.getQualityModel().getProperty("Property 02");

        p1.getMeasure().setNormalizedValue(0.51);
        p2.getMeasure().setNormalizedValue(0.99);

        p.evaluateProperties();

        Assert.assertEquals(0.0, p1.getValue(), 0);
        Assert.assertEquals(0.0, p2.getValue(), 0);
    }

    @Test
    public void testEvaluateProperties_negative_upperGroup() {
        Property p1 = p.getQualityModel().getProperty("Property 01");
        Property p2 = p.getQualityModel().getProperty("Property 02");

        p1.getMeasure().setNormalizedValue(0.21);
        p2.getMeasure().setNormalizedValue(0.49);

        p.evaluateProperties();

        Assert.assertEquals(0.483, p1.getValue(), .001);
        Assert.assertEquals(0.016, p2.getValue(), .001);
    }

    @Test
    public void testEvaluateProperties_positive_lowerGroup() {
        Property p1 = p.getQualityModel().getProperty("Property 01");
        Property p2 = p.getQualityModel().getProperty("Property 02");

        p1.setPositive(true);
        p2.setPositive(true);
        p1.getMeasure().setNormalizedValue(0.01);
        p2.getMeasure().setNormalizedValue(0.09);

        p.evaluateProperties();

        Assert.assertEquals(0, p1.getValue(), 0);
        Assert.assertEquals(0, p2.getValue(), 0);
    }

    @Test
    public void testEvaluateProperties_positive_middleGroup() {
        Property p1 = p.getQualityModel().getProperty("Property 01");
        Property p2 = p.getQualityModel().getProperty("Property 02");

        p1.setPositive(true);
        p2.setPositive(true);
        p1.getMeasure().setNormalizedValue(0.11);
        p2.getMeasure().setNormalizedValue(0.19);

        p.evaluateProperties();

        Assert.assertEquals(0.05, p1.getValue(), .001);
        Assert.assertEquals(0.45, p2.getValue(), .001);
    }

    @Test
    public void testEvaluateProperties_positive_saturation() {
        Property p1 = p.getQualityModel().getProperty("Property 01");
        Property p2 = p.getQualityModel().getProperty("Property 02");

        p1.setPositive(true);
        p2.setPositive(true);
        p1.getMeasure().setNormalizedValue(0.51);
        p2.getMeasure().setNormalizedValue(0.99);

        p.evaluateProperties();

        Assert.assertEquals(1.0, p1.getValue(), 0);
        Assert.assertEquals(1.0, p2.getValue(), 0);
    }

    @Test
    public void testEvaluateProperties_positive_upperGroup() {
        Property p1 = p.getQualityModel().getProperty("Property 01");
        Property p2 = p.getQualityModel().getProperty("Property 02");

        p1.setPositive(true);
        p2.setPositive(true);
        p1.getMeasure().setNormalizedValue(0.21);
        p2.getMeasure().setNormalizedValue(0.49);

        p.evaluateProperties();

        Assert.assertEquals(0.516, p1.getValue(), .001);
        Assert.assertEquals(0.983, p2.getValue(), .001);
    }

    @Test
    public void testEvaluateTqi() {
        p.getQualityModel().getCharacteristic("Characteristic 01").setValue(0.1);
        p.getQualityModel().getCharacteristic("Characteristic 02").setValue(0.9);
        p.evaluateTqi();

        Assert.assertEquals(0.34, p.getQualityModel().getTqi().getValue(), 0.001);

    }

    @Test
    public void testExportEvaluation() throws IOException {
        Path exportLocation = Paths.get("src/test/out/TestExportEval");

        Path result = p.exportToJson(exportLocation);
        FileReader fr = new FileReader(result.toString());
        JsonObject jsonResults = new JsonParser().parse(fr).getAsJsonObject();
        fr.close();

        int loc = jsonResults.getAsJsonPrimitive("linesOfCode").getAsInt();
        String name = jsonResults.getAsJsonPrimitive("name").getAsString();
        int numCharacteristics = jsonResults.getAsJsonObject("characteristics").size();
        int numProperties = jsonResults.getAsJsonObject("properties").size();
        double tqiValue = jsonResults.getAsJsonObject("tqi").getAsJsonPrimitive("value").getAsDouble();

        Assert.assertEquals(100, loc);
        Assert.assertEquals("TestProject", name);
        Assert.assertEquals(2, numCharacteristics);
        Assert.assertEquals(2, numProperties);
        Assert.assertEquals(0.92, tqiValue, 0);

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
