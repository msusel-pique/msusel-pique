package qatch;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Assert;
import org.junit.Test;
import qatch.calibration.BenchmarkAnalysisExporter;
import qatch.calibration.BenchmarkProjects;
import qatch.calibration.RInvoker;
import qatch.evaluation.Project;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CalibrationTests {

    /*
     * BenchmarkAnalysisExporter
     */
    @Test
    public void testExportToXls() throws IOException {
        Project proj1 = TestHelper.makeProject("Project 01");
        proj1.getProperties().get(0).getMeasure().setNormValue(0.11);
        proj1.getProperties().get(1).getMeasure().setNormValue(0.12);

        Project proj2 = TestHelper.makeProject("Project 02");
        proj2.getProperties().get(0).getMeasure().setNormValue(0.21);
        proj2.getProperties().get(1).getMeasure().setNormValue(0.22);

        Project proj3 = TestHelper.makeProject("Project 03");
        proj3.getProperties().get(0).getMeasure().setNormValue(0.31);
        proj3.getProperties().get(1).getMeasure().setNormValue(0.32);

        BenchmarkProjects benchProjs = new BenchmarkProjects();
        benchProjs.addProject(proj1);
        benchProjs.addProject(proj2);
        benchProjs.addProject(proj3);

        new BenchmarkAnalysisExporter().exportToXls(benchProjs);

        // TODO: keep test files in test output folder
        File file = new File(RInvoker.R_WORK_DIR.toFile(), "properties.xls");
        FileInputStream fis = new FileInputStream(file);
        HSSFWorkbook wb = new HSSFWorkbook(fis);
        HSSFSheet sh = wb.getSheetAt(0);

        Assert.assertEquals(0.11, sh.getRow(1).getCell(1).getNumericCellValue(),  0.0);
        Assert.assertEquals(0.12, sh.getRow(1).getCell(2).getNumericCellValue(),  0.0);

        Assert.assertEquals(0.21, sh.getRow(2).getCell(1).getNumericCellValue(),  0.0);
        Assert.assertEquals(0.22, sh.getRow(2).getCell(2).getNumericCellValue(),  0.0);

        Assert.assertEquals(0.31, sh.getRow(3).getCell(1).getNumericCellValue(),  0.0);
        Assert.assertEquals(0.32, sh.getRow(3).getCell(2).getNumericCellValue(),  0.0);
    }

    /*
     * RInvoker
     */
    @Test
    public void testExecuteRScriptForThresholds() throws IOException {

        TestHelper.clean(TestHelper.OUTPUT.toFile());

        // Mock benchmark analysis results
        TestHelper.OUTPUT.toFile().mkdirs();
        String filename = TestHelper.OUTPUT + "/properties.xls";

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Benchmark Analysis Results");

        HSSFRow rowhead = sheet.createRow((short) 0);
        rowhead.createCell(0).setCellValue("Project_Name");
        rowhead.createCell(1).setCellValue("Property 01");
        rowhead.createCell(2).setCellValue("Property 02");
        rowhead.createCell(3).setCellValue("Property 03");

        HSSFRow row1 = sheet.createRow(1);
        row1.createCell(0).setCellValue("Project 01");
        row1.createCell(1).setCellValue(.010);
        row1.createCell(2).setCellValue(.07);
        row1.createCell(3).setCellValue(.091);
        HSSFRow row2 = sheet.createRow(2);
        row2.createCell(0).setCellValue("Project 02");
        row2.createCell(1).setCellValue(.050);
        row2.createCell(2).setCellValue(.069);
        row2.createCell(3).setCellValue(.096);
        HSSFRow row3 = sheet.createRow(3);
        row3.createCell(0).setCellValue("Project 03");
        row3.createCell(1).setCellValue(.013);
        row3.createCell(2).setCellValue(.050);
        row3.createCell(3).setCellValue(.001);
        HSSFRow row4 = sheet.createRow(4);
        row4.createCell(0).setCellValue("Project 04");
        row4.createCell(1).setCellValue(.019);
        row4.createCell(2).setCellValue(.068);
        row4.createCell(3).setCellValue(.093);
        HSSFRow row5 = sheet.createRow(5);
        row5.createCell(0).setCellValue("Project 05");
        row5.createCell(1).setCellValue(.022);
        row5.createCell(2).setCellValue(.059);
        row5.createCell(3).setCellValue(.099);

        //Export the XLS file to the appropriate path
        FileOutputStream fileOut = null;
        fileOut = new FileOutputStream(filename);
        workbook.write(fileOut);
        fileOut.close();

        // run R Executions
        RInvoker rInvoker = new RInvoker();
        Path script = new File(RInvoker.getRScriptResource(RInvoker.Script.THRESHOLD).getFile()).toPath();
        rInvoker.executeRScript(
                RInvoker.R_BIN_PATH,
                script,
                TestHelper.OUTPUT.toString()
        );

        if (!(new File(TestHelper.OUTPUT.toFile(), "threshold.json").isFile())) {
            Assert.fail("R execution did not generate the expected file. "
            + "Have the necessary libraries been downloaded for R?");
        }

        JsonParser parser = new JsonParser();
        JsonArray data = (JsonArray) parser.parse(new FileReader(new File(TestHelper.OUTPUT.toString() + "/threshold.json")));

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
