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

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ProjectTests {

    Project p;

    @Before
    public void setUp()  {
        this.p = TestHelper.makeProject("TestProject");
    }


    @Test
    public void testEvaluateMeasures() {
        Measure m1 = p.getMeasure("Property 01");
        Measure m2 = p.getMeasure("Property 02");

        p.evaluateMeasures();

        Assert.assertEquals(0.04, m1.getNormalizedValue(), 0);
        Assert.assertEquals(0.04, m2.getNormalizedValue(), 0);
    }

    @Test
    public void testEvaluateProperties_positive_lowerGroup() {
        Property p1 = p.getProperty("Property 01");
        Property p2 = p.getProperty("Property 01");

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
        Property p1 = p.getProperty("Property 01");
        Property p2 = p.getProperty("Property 01");

        p1.setPositive(true);
        p2.setPositive(true);
        p1.getMeasure().setNormalizedValue(0.11);
        p2.getMeasure().setNormalizedValue(0.19);

        p.evaluateProperties();

        Assert.assertEquals(0.05, p1.getValue(), 0);
        Assert.assertEquals(0.45, p2.getValue(), 0);
    }

    @Test
    public void testExportEvaluation() throws IOException {
        Path exportLocation = Paths.get("src/test/output/TestExportEval");

        Path result = p.exportEvaluation(exportLocation);
        FileReader fr = new FileReader(result.toString());
        JsonObject jsonResults = new JsonParser().parse(fr).getAsJsonObject();
        fr.close();

        int loc = jsonResults.getAsJsonPrimitive("linesOfCode").getAsInt();
        String name = jsonResults.getAsJsonPrimitive("name").getAsString();
        int numCharacteristics = jsonResults.getAsJsonObject("characteristics").size();
        int numProperties = jsonResults.getAsJsonObject("properties").size();
        double tqiValue = jsonResults.getAsJsonObject("tqi").getAsJsonPrimitive("value").getAsDouble();

        Assert.assertEquals(555, loc);
        Assert.assertEquals("TestProject", name);
        Assert.assertEquals(2, numCharacteristics);
        Assert.assertEquals(2, numProperties);
        Assert.assertEquals(0.92, tqiValue, 0);

        FileUtils.forceDelete(exportLocation.toFile());
    }
}
