package qatch.evaluation;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import qatch.TestHelper;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ProjectTests {


    @Test
    public void testExportEvaluation() throws IOException {
        Project p = TestHelper.makeProject("TestProject");
        Path exportLocation = Paths.get("src/test/output/TestExportEval");

        Path result = p.exportEvaluation(exportLocation);
        FileReader fr = new FileReader(result.toString());
        JsonObject jsonResults = new JsonParser().parse(fr).getAsJsonObject();
        fr.close();

        int loc = jsonResults.getAsJsonPrimitive("linesOfCode").getAsInt();
        String name = jsonResults.getAsJsonPrimitive("name").getAsString();
//        int numCharacteristics = jsonResults.getAsJsonObject("characteristics").size();
//        int numProperties = jsonResults.getAsJsonObject("properties").size();
        double tqiValue = jsonResults.getAsJsonObject("tqi").getAsJsonPrimitive("value").getAsDouble();

        Assert.assertEquals(555, loc);
        Assert.assertEquals("TestProject", name);
//        Assert.assertEquals(2, numCharacteristics);
//        Assert.assertEquals(2, numProperties);
        Assert.assertEquals(0.92, tqiValue, 0);

        FileUtils.forceDelete(exportLocation.toFile());
    }
}
