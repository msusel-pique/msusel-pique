package qatch.calibration;

import com.opencsv.CSVReader;
import org.junit.Assert;
import qatch.TestHelper;
import qatch.evaluation.Project;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

public class BenchmarkAnalysisExporterTests {

//    @Test
    public void testExportToCsv() throws IOException {
        Project proj1 = TestHelper.makeProject("Project 01");
        proj1.getProperties_depreicated().get(0).getMeasure().setNormValue(0.11);
        proj1.getProperties_depreicated().get(1).getMeasure().setNormValue(0.12);

        Project proj2 = TestHelper.makeProject("Project 02");
        proj2.getProperties_depreicated().get(0).getMeasure().setNormValue(0.21);
        proj2.getProperties_depreicated().get(1).getMeasure().setNormValue(0.22);

        Project proj3 = TestHelper.makeProject("Project 03");
        proj3.getProperties_depreicated().get(0).getMeasure().setNormValue(0.31);
        proj3.getProperties_depreicated().get(1).getMeasure().setNormValue(0.32);

        BenchmarkProjects benchProjs = new BenchmarkProjects();
        benchProjs.addProject(proj1);
        benchProjs.addProject(proj2);
        benchProjs.addProject(proj3);

        Path csvResults = new BenchmarkAnalysisExporter().exportToCsv(benchProjs);

        CSVReader reader = new CSVReader(new FileReader(csvResults.toFile()));
        String[] header = reader.readNext();
        String[] proj1Values = reader.readNext();
        String[] proj2Values = reader.readNext();
        String[] proj3Values = reader.readNext();

        Assert.assertEquals(4, (int)reader.getLinesRead());

        Assert.assertTrue(header[0].equalsIgnoreCase("Project_Name"));
        Assert.assertTrue(header[1].equalsIgnoreCase("Property 01"));
        Assert.assertTrue(header[2].equalsIgnoreCase("Property 02"));

        Assert.assertTrue(proj1Values[0].equalsIgnoreCase("Project 01"));
        Assert.assertEquals(0.11, Double.parseDouble(proj1Values[1]), 0.0001);
        Assert.assertEquals(0.12, Double.parseDouble(proj1Values[2]), 0.0001);

        Assert.assertTrue(proj2Values[0].equalsIgnoreCase("Project 02"));
        Assert.assertEquals(0.21, Double.parseDouble(proj2Values[1]), 0.0001);
        Assert.assertEquals(0.22, Double.parseDouble(proj2Values[2]), 0.0001);

        Assert.assertTrue(proj3Values[0].equalsIgnoreCase("Project 03"));
        Assert.assertEquals(0.31, Double.parseDouble(proj3Values[1]), 0.0001);
        Assert.assertEquals(0.32, Double.parseDouble(proj3Values[2]), 0.0001);

        reader.close();
    }
}
