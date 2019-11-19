package qatch.runnable;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import qatch.TestHelper;
import qatch.evaluation.Project;
import qatch.model.QualityModel;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SingleProjectEvaluatorTests {

    private SingleProjectEvaluator spe;
    private Path PROJECT_DIR = Paths.get("src/test/resources/FakeProject_01");
    private Path RESULTS_DIR = Paths.get("src/test/out/SingleProjEval");
    private Path QM_LOCATION = Paths.get("src/test/resources/quality_models/qualityModel_test.json");
    private Path TOOL_RESULTS = Paths.get("src/test/resources/tool_results");
    private Path TEST_OUT = Paths.get("src/test/out");


    @Before
    public void cleanBefore() throws IOException {
        TestHelper.cleanTestOutput();
    }

    @Before
    public void initClass() {
        spe = new SingleProjectEvaluator();
    }

    @After
    public void cleanAfter() throws IOException {
        TestHelper.cleanTestOutput();
    }

    @Test
    public void testAggregateNormalize() {
        // TODO: test might not be worth writing due to interface call
        Assert.assertTrue(true);
    }

//    @Test
    public void testEvaluate() {
        Project p = TestHelper.makeProject("Test Project");
        // temp fix to avoid QM clone problem
//        p.getCharacteristics_depreicated().removeCharacteristic(0);
//        p.getCharacteristics_depreicated().removeCharacteristic(0);

        QualityModel qm = new QualityModel(QM_LOCATION);

        // TODO: add more edge cases
//        p.getProperties_depreicated().get(0).getMeasure().setNormalizedValue(0.90);
//        p.getProperties_depreicated().get(1).getMeasure().setNormalizedValue(0.10);

//        spe.evaluate_deprecated(p, qm);

//        Assert.assertEquals(0.099999, p.getProperties_depreicated().get(0).getValue(),0.00001);
//        Assert.assertEquals(0.9, p.getProperties_depreicated().get(1).getValue(),0.00001);

//        Assert.assertEquals(0.42, p.getCharacteristics_depreicated().get(0).getValue(), 0.00001);
//        Assert.assertEquals(0.50, p.getCharacteristics_depreicated().get(1).getValue(), 0.00001);

        Assert.assertEquals(0.436, p.getTqi().getValue(),0.00001);
    }


    @Test
    public void testInitialize() {
        try { spe.initialize(PROJECT_DIR, RESULTS_DIR, QM_LOCATION); }
        catch (IllegalArgumentException e) { Assert.fail(e.getMessage()); }

        try {
            spe.initialize(
                    Paths.get("src/test/resources/IDONTEXIST"),
                    Paths.get("src/test/out/SingleProjEval"),
                    QM_LOCATION
            );
        } catch (IllegalArgumentException ignored) {  }
    }

    @Test
    public void testRunEvaluator() throws IOException {

        // Run single project evaluator
        SingleProjectEvaluator spe = new SingleProjectEvaluator();
        Path result = spe.runEvaluator(PROJECT_DIR, RESULTS_DIR, QM_LOCATION, TestHelper.makeITool(), TestHelper.makeIToolLoc());

        // Read result file
        FileReader fr = new FileReader(result.toString());
        JsonObject jsonResults = new JsonParser().parse(fr).getAsJsonObject();
        fr.close();

        // Assign parsed results
        String projectName = jsonResults.get("name").getAsString();
        Integer loc = jsonResults.get("linesOfCode").getAsInt();
        JsonObject tqi = jsonResults.getAsJsonObject("tqi");
        JsonObject characteristic01 = jsonResults.getAsJsonObject("characteristics").getAsJsonObject("Characteristic 01");
        JsonObject characteristic02 = jsonResults.getAsJsonObject("characteristics").getAsJsonObject("Characteristic 02");
        JsonObject property01 = jsonResults.getAsJsonObject("properties").getAsJsonObject("Property 01");
        JsonObject property02 = jsonResults.getAsJsonObject("properties").getAsJsonObject("Property 02");
        JsonObject measure01 = property01.getAsJsonObject("measure");
        JsonObject measure02 = property02.getAsJsonObject("measure");

        // Assert: project basics
        Assert.assertEquals("FakeProject_01", projectName);
        Assert.assertEquals(1000, loc, 0);

        // Assert: diagnostics
        Assert.assertEquals(2, measure01.getAsJsonArray("diagnostics").size());
        Assert.assertEquals(2.0, measure01.getAsJsonArray("diagnostics").get(0).getAsJsonObject().get("value").getAsDouble(), 0);

        // Assert: measure nodes
        Assert.assertEquals("Measure 01", measure01.get("name").getAsString());
        Assert.assertEquals("Measure 02", measure02.get("name").getAsString());
        Assert.assertEquals(0.004, measure01.get("normalizedValue").getAsDouble(), 0.000001);
        Assert.assertEquals(0.004, measure02.get("normalizedValue").getAsDouble(), 0.000001);

        // Assert: property nodes
        Assert.assertEquals("Property 01", property01.get("name").getAsString());
        Assert.assertEquals("Property 02", property02.get("name").getAsString());
        Assert.assertEquals(0.5, property01.get("value").getAsDouble(), 0.000001);
        Assert.assertEquals(0.8, property02.get("value").getAsDouble(), 0.000001);

        // Assert: characteristics nodes
        Assert.assertEquals("Characteristic 01", characteristic01.get("name").getAsString());
        Assert.assertEquals("Characteristic 02", characteristic02.get("name").getAsString());
        Assert.assertEquals(0.62, characteristic01.get("value").getAsDouble(), 0.000001);
        Assert.assertEquals(0.65, characteristic02.get("value").getAsDouble(), 0.000001);

        // Assert: TQI node
        Assert.assertEquals("Total Quality", tqi.get("name").getAsString());
        Assert.assertEquals(0.626, tqi.get("value").getAsDouble(), 0.000001);
    }
}
