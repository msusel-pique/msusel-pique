package qatch.calibration;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import qatch.TestHelper;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RInvokerTests {

    private Path comp_matrix = Paths.get(TestHelper.TEST_RESOURCES.toString(),
            "comparison_matrices",
            "TQI.csv");

    @Before
    public void cleanBefore() throws IOException {
        TestHelper.cleanTestOutput();
    }

    @After
    public void cleanAfter() throws IOException {
        TestHelper.cleanTestOutput();
    }

    @Test
    public void textExecuteRScriptForAHPElicitation() throws IOException {

        // set up R environment
        File matrixDir = new File(TestHelper.OUTPUT.toFile(), "comparison_matrices");
        matrixDir.mkdirs();
        FileUtils.copyFileToDirectory(comp_matrix.toFile(), matrixDir);
        File weightsOutput = new File(TestHelper.OUTPUT.toFile(), "weights.json");

        // run R execution
        RInvoker ri = new RInvoker();
        Path script = new File(RInvoker.getRScriptResource(RInvoker.Script.AHP).getFile()).toPath();
        ri.executeRScript(
                RInvoker.R_BIN_PATH,
                script,
                TestHelper.OUTPUT.toString()
        );

        if (!weightsOutput.isFile()) {
            Assert.fail("R execution did not generate the expected file. "
                    + "Have the necessary libraries been downloaded for R?");
        }

        try (JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(weightsOutput)))) {
            JsonParser parser = new JsonParser();
            JsonObject data = parser.parse(reader).getAsJsonObject();

            float weight1 = data.getAsJsonArray("TQI").get(0).getAsFloat();
            float weight2 = data.getAsJsonArray("TQI").get(1).getAsFloat();

            Assert.assertEquals(0.20, weight1, 0.00001);
            Assert.assertEquals(0.80, weight2, 0.00001);
        }
    }

    @Test
    public void testExecuteRScriptForThresholds() throws IOException {

        // Mock benchmark analysis results
        TestHelper.OUTPUT.toFile().mkdirs();
        FileUtils.copyFileToDirectory(
                new File(TestHelper.TEST_RESOURCES.toFile(), "benchmark_results/properties.csv"),
                TestHelper.OUTPUT.toFile()
        );
        File thresholdOutput = new File(TestHelper.OUTPUT.toFile(), "threshold.json");

        // run R Executions
        RInvoker rInvoker = new RInvoker();
        Path script = new File(RInvoker.getRScriptResource(RInvoker.Script.THRESHOLD).getFile()).toPath();
        rInvoker.executeRScript(
                RInvoker.R_BIN_PATH,
                script,
                TestHelper.OUTPUT.toString()
        );

        if (!thresholdOutput.isFile()) {
            Assert.fail("R execution did not generate the expected file. "
                    + "Have the necessary libraries been downloaded for R?");
        }

        try (JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(thresholdOutput)))) {
            JsonParser parser = new JsonParser();
            JsonArray data = parser.parse(reader).getAsJsonArray();

            float p1t1 = data.get(0).getAsJsonObject().get("t1").getAsFloat();
            float p1t2 = data.get(0).getAsJsonObject().get("t2").getAsFloat();
            float p1t3 = data.get(0).getAsJsonObject().get("t3").getAsFloat();

            float p2t1 = data.get(1).getAsJsonObject().get("t1").getAsFloat();
            float p2t2 = data.get(1).getAsJsonObject().get("t2").getAsFloat();
            float p2t3 = data.get(1).getAsJsonObject().get("t3").getAsFloat();

            float p3t1 = data.get(2).getAsJsonObject().get("t1").getAsFloat();
            float p3t2 = data.get(2).getAsJsonObject().get("t2").getAsFloat();
            float p3t3 = data.get(2).getAsJsonObject().get("t3").getAsFloat();

            Assert.assertEquals(0.010, p1t1, 0.000001);
            Assert.assertEquals(0.019, p1t2, 0.000001);
            Assert.assertEquals(0.022, p1t3, 0.000001);

            Assert.assertEquals(0.050, p2t1, 0.000001);
            Assert.assertEquals(0.068, p2t2, 0.000001);
            Assert.assertEquals(0.070, p2t3, 0.000001);

            Assert.assertEquals(0.091, p3t1, 0.000001);
            Assert.assertEquals(0.093, p3t2, 0.000001);
            Assert.assertEquals(0.099, p3t3, 0.000001);
        }
    }

    @Test
    public void testGetRScriptResource() {
        URL ahp = RInvoker.getRScriptResource(RInvoker.Script.AHP);
        URL faph = RInvoker.getRScriptResource(RInvoker.Script.FAPH);
        URL threshold = RInvoker.getRScriptResource(RInvoker.Script.THRESHOLD);

        File ahpFile = new File(ahp.getFile());
        File faphFile = new File(faph.getFile());
        File tFile = new File(threshold.getFile());

        Assert.assertTrue(ahpFile.exists());
        Assert.assertTrue(ahpFile.isFile());
        Assert.assertTrue(faphFile.exists());
        Assert.assertTrue(faphFile.isFile());
        Assert.assertTrue(tFile.exists());
        Assert.assertTrue(tFile.isFile());
    }

}
