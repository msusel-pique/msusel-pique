package qatch.runnable;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import qatch.TestHelper;

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

        JsonObject additionalData = jsonResults.getAsJsonObject("additionalData");
        JsonObject jsonTqi = jsonResults.getAsJsonObject("tqi");
        JsonObject jsonCharacteristics = jsonResults.getAsJsonObject("characteristics");
        JsonObject jsonChar01 = jsonCharacteristics.getAsJsonObject("Characteristic 01");
        JsonObject jsonChar02 = jsonCharacteristics.getAsJsonObject("Characteristic 02");
        JsonObject jsonProperties = jsonResults.getAsJsonObject("properties");
        JsonObject jsonProperty01 = jsonProperties.getAsJsonObject("Property 01");
        JsonObject jsonProperty02 = jsonProperties.getAsJsonObject("Property 02");
        JsonObject jsonMeasure01 = jsonProperty01.getAsJsonObject("measure");
        JsonObject jsonMeasure02 = jsonProperty02.getAsJsonObject("measure");

        // Asserts
        Assert.assertEquals("FakeProject_01", additionalData.getAsJsonPrimitive("projectName").getAsString());
        Assert.assertEquals("200", additionalData.getAsJsonPrimitive("projectLinesOfCode").getAsString());

        Assert.assertEquals("Test QM", jsonResults.getAsJsonPrimitive("name").getAsString());

        Assert.assertEquals("Total Quality", jsonTqi.getAsJsonPrimitive("name").getAsString());
        Assert.assertEquals(0.586875, jsonTqi.getAsJsonPrimitive("value").getAsFloat(), 0.0001);
        Assert.assertEquals(0.8, jsonTqi.getAsJsonObject("weights").getAsJsonPrimitive("Characteristic 01").getAsDouble(), 0.0001);
        Assert.assertEquals(0.2, jsonTqi.getAsJsonObject("weights").getAsJsonPrimitive("Characteristic 02").getAsDouble(), 0.0001);

        Assert.assertEquals(0.58125, jsonChar01.getAsJsonPrimitive("value").getAsFloat(), 0.0001);
        Assert.assertEquals(0.6, jsonChar01.getAsJsonObject("weights").getAsJsonPrimitive("Property 01").getAsDouble(), 0.0001);
        Assert.assertEquals(0.4, jsonChar01.getAsJsonObject("weights").getAsJsonPrimitive("Property 02").getAsDouble(), 0.0001);

        Assert.assertEquals(0.609375, jsonChar02.getAsJsonPrimitive("value").getAsFloat(), 0.0001);
        Assert.assertEquals(0.5, jsonChar02.getAsJsonObject("weights").getAsJsonPrimitive("Property 01").getAsDouble(), 0.0001);
        Assert.assertEquals(0.5, jsonChar02.getAsJsonObject("weights").getAsJsonPrimitive("Property 02").getAsDouble(), 0.0001);

        Assert.assertEquals(0.46875, jsonProperty01.getAsJsonPrimitive("value").getAsFloat(), 0.0001);
        Assert.assertEquals(0.0, jsonProperty01.getAsJsonArray("thresholds").get(0).getAsFloat(), 0.0001);
        Assert.assertEquals(0.004, jsonProperty01.getAsJsonArray("thresholds").get(1).getAsFloat(), 0.0001);
        Assert.assertEquals(0.02, jsonProperty01.getAsJsonArray("thresholds").get(2).getAsFloat(), 0.0001);

        Assert.assertEquals(0.75, jsonProperty02.getAsJsonPrimitive("value").getAsFloat(), 0.0001);
        Assert.assertEquals(0.0, jsonProperty02.getAsJsonArray("thresholds").get(0).getAsFloat(), 0.0001);
        Assert.assertEquals(0.01, jsonProperty02.getAsJsonArray("thresholds").get(1).getAsFloat(), 0.0001);
        Assert.assertEquals(0.02, jsonProperty02.getAsJsonArray("thresholds").get(2).getAsFloat(), 0.0001);

        Assert.assertEquals(0.005, jsonMeasure01.getAsJsonPrimitive("value").getAsFloat(), 0.0001);
        Assert.assertEquals(0.005, jsonMeasure02.getAsJsonPrimitive("value").getAsFloat(), 0.0001);
    }
}
