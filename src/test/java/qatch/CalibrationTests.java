package qatch;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Assert;
import org.junit.Test;
import qatch.calibration.BenchmarkAnalysisExporter;
import qatch.calibration.BenchmarkProjects;
import qatch.evaluation.Project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class CalibrationTests {

    /*
     * BenchmarkAnalysisExporter
     */
    @Test
    public void testExportToXls() throws IOException {
        Project proj1 = TestObjects.makeProject("Project 01");
        proj1.getProperties().get(0).getMeasure().setNormValue(0.11);
        proj1.getProperties().get(1).getMeasure().setNormValue(0.12);

        Project proj2 = TestObjects.makeProject("Project 02");
        proj2.getProperties().get(0).getMeasure().setNormValue(0.21);
        proj2.getProperties().get(1).getMeasure().setNormValue(0.22);

        Project proj3 = TestObjects.makeProject("Project 03");
        proj3.getProperties().get(0).getMeasure().setNormValue(0.31);
        proj3.getProperties().get(1).getMeasure().setNormValue(0.32);

        BenchmarkProjects benchProjs = new BenchmarkProjects();
        benchProjs.addProject(proj1);
        benchProjs.addProject(proj2);
        benchProjs.addProject(proj3);

        new BenchmarkAnalysisExporter().exportToXls(benchProjs);

        File file = new File("r_working_directory/properties.xls");
        FileInputStream fis = new FileInputStream(file);
        HSSFWorkbook wb = new HSSFWorkbook(fis);
        HSSFSheet sh = wb.getSheetAt(0);

        Assert.assertEquals(0.11, sh.getRow(1).getCell(0).getNumericCellValue(),  0.0);
        Assert.assertEquals(0.12, sh.getRow(1).getCell(1).getNumericCellValue(),  0.0);

        Assert.assertEquals(0.21, sh.getRow(2).getCell(0).getNumericCellValue(),  0.0);
        Assert.assertEquals(0.22, sh.getRow(2).getCell(1).getNumericCellValue(),  0.0);

        Assert.assertEquals(0.31, sh.getRow(3).getCell(0).getNumericCellValue(),  0.0);
        Assert.assertEquals(0.32, sh.getRow(3).getCell(1).getNumericCellValue(),  0.0);
    }
}
