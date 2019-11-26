package qatch.model;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class QualityModelExportTests {

    private Path qmTest = Paths.get("src/test/resources/quality_models/qualityModel_test.json");

    @Test
    public void testExportToJson() throws IOException {
        String exportName = "qualityModel_test_export";
        Path exportLocation = Paths.get("src/test/out/TestExportEval");

        QualityModel qm = new QualityModel(qmTest);
        QualityModelExport qmExport = new QualityModelExport(qm);

        Path result = qmExport.exportToJson(exportName, exportLocation);
        FileReader fr = new FileReader(result.toString());
        JsonObject jsonResults = new JsonParser().parse(fr).getAsJsonObject();
        fr.close();
        FileUtils.forceDelete(exportLocation.toFile());

        String name = jsonResults.getAsJsonPrimitive("name").getAsString();
        JsonObject jsonTqi = jsonResults.getAsJsonObject("tqi");
        JsonObject jsonCharacteristics = jsonResults.getAsJsonObject("characteristics");
        JsonObject jsonChar01 = jsonCharacteristics.getAsJsonObject("Characteristic 01");
        JsonObject jsonChar02 = jsonCharacteristics.getAsJsonObject("Characteristic 02");
        JsonObject jsonProperties = jsonResults.getAsJsonObject("properties");
        JsonObject jsonProp01 = jsonProperties.getAsJsonObject("Property 01");
        JsonObject jsonProp02 = jsonProperties.getAsJsonObject("Property 02");

        Assert.assertEquals("Test QM", name);

        Assert.assertEquals("Total Quality", jsonTqi.getAsJsonPrimitive("name").getAsString());
        Assert.assertEquals(0.8, jsonTqi.getAsJsonObject("weights").getAsJsonPrimitive("Characteristic 01").getAsDouble(), 0.0001);
        Assert.assertEquals(0.2, jsonTqi.getAsJsonObject("weights").getAsJsonPrimitive("Characteristic 02").getAsDouble(), 0.0001);

        Assert.assertEquals(0.6, jsonChar01.getAsJsonObject("weights").getAsJsonPrimitive("Property 01").getAsDouble(), 0.0001);
        Assert.assertEquals(0.4, jsonChar01.getAsJsonObject("weights").getAsJsonPrimitive("Property 02").getAsDouble(), 0.0001);

        Assert.assertEquals(0.5, jsonChar02.getAsJsonObject("weights").getAsJsonPrimitive("Property 01").getAsDouble(), 0.0001);
        Assert.assertEquals(0.5, jsonChar02.getAsJsonObject("weights").getAsJsonPrimitive("Property 02").getAsDouble(), 0.0001);

        Assert.assertEquals(0.0, jsonProp01.getAsJsonArray("thresholds").get(0).getAsFloat(), 0.0001);
        Assert.assertEquals(0.004, jsonProp01.getAsJsonArray("thresholds").get(1).getAsFloat(), 0.0001);
        Assert.assertEquals(0.02, jsonProp01.getAsJsonArray("thresholds").get(2).getAsFloat(), 0.0001);

        Assert.assertEquals(0.0, jsonProp02.getAsJsonArray("thresholds").get(0).getAsFloat(), 0.0001);
        Assert.assertEquals(0.01, jsonProp02.getAsJsonArray("thresholds").get(1).getAsFloat(), 0.0001);
        Assert.assertEquals(0.02, jsonProp02.getAsJsonArray("thresholds").get(2).getAsFloat(), 0.0001);


    }

}
