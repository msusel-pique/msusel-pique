package qatch.calibration;

import com.opencsv.CSVReader;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import qatch.TestHelper;
import qatch.analysis.Finding;
import qatch.analysis.Measure;
import qatch.evaluation.Project;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BenchmarkerTests {

    private Path analysisResultsAsInput = Paths.get("src/test/resources/benchmark_results/benchmark_data.csv");
    private Path analysisResultsAsOutput = Paths.get("src/test/out/benchmark_results/benchmark_data.csv");
    private Path benchmarkRepo = Paths.get("src/test/resources/benchmark_repository");
    private Path qmDescription = Paths.get("src/test/resources/quality_models/qualityModel_test_description.json");
    private Path rThresholdsOutput = Paths.get("src/test/out/r_thresholds");


    @Test
    public void testCreateProjectMeasureMatrix() throws IOException {

        // Set-up
        Finding f1 = TestHelper.makeFinding("file/path/finding1", 11, 1);
        Finding f2 = TestHelper.makeFinding("file/path/finding2", 22, 1);
        Finding f3 = TestHelper.makeFinding("file/path/finding3", 33, 1);

        int linesOfCode = 9;
        Project project01 = TestHelper.makeProject("Project 01");
        project01.setLinesOfCode(linesOfCode);
        Project project02 = TestHelper.makeProject("Project 02");
        project02.setLinesOfCode(linesOfCode);
        Project project03 = TestHelper.makeProject("Project 03");
        project03.setLinesOfCode(linesOfCode);
        HashMap<String, Project> projects = new HashMap<>();

        /*
         * Test info for reference and assertions:
         *
         * Project 01: Measure 01 = 0.11, Measure 02 = 0.11
         *      Property 01 measure diagnostic01: 1 finding
         *      Property 01 measure diagnostic02: 0 finding
         *      Property 02 measure diagnostic01: 1 finding
         *      Property 02 measure diagnostic02: 0 finding
         * Project 02: Measure 01 = 0.33, Measure 02 = 0.3
         *      Property 01 measure diagnostic01: 2 findings
         *      Property 01 measure diagnostic02: 1 findings
         *      Property 02 measure diagnostic01: 2 findings
         *      Property 02 measure diagnostic02: 1 findings
         * Project 03: Measure 01 = 0.44, Measure 02 = 0.44
         *      Property 01 measure diagnostic01: 1 findings
         *      Property 01 measure diagnostic02: 3 findings
         *      Property 02 measure diagnostic01: 1 findings
         *      Property 02 measure diagnostic02: 3 findings
         */
        Measure proj01measure01 = project01.getQualityModel().getMeasure("Property 01 measure");
        Measure proj01measure02 = project01.getQualityModel().getMeasure("Property 02 measure");
        proj01measure01.getDiagnostic("Property 01 measure diagnostic01").setFinding(f1);
        proj01measure02.getDiagnostic("Property 02 measure diagnostic01").setFinding(f1);

        Measure proj02measure01 = project02.getQualityModel().getMeasure("Property 01 measure");
        Measure proj02measure02 = project02.getQualityModel().getMeasure("Property 02 measure");
        proj02measure01.getDiagnostic("Property 01 measure diagnostic01").setFinding(f1);
        proj02measure01.getDiagnostic("Property 01 measure diagnostic01").setFinding(f2);
        proj02measure01.getDiagnostic("Property 01 measure diagnostic02").setFinding(f1);
        proj02measure02.getDiagnostic("Property 02 measure diagnostic01").setFinding(f1);
        proj02measure02.getDiagnostic("Property 02 measure diagnostic01").setFinding(f2);
        proj02measure02.getDiagnostic("Property 02 measure diagnostic02").setFinding(f1);

        Measure proj03measure01 = project03.getQualityModel().getMeasure("Property 01 measure");
        Measure proj03measure02 = project03.getQualityModel().getMeasure("Property 02 measure");
        proj03measure01.getDiagnostic("Property 01 measure diagnostic01").setFinding(f1);
        proj03measure01.getDiagnostic("Property 01 measure diagnostic02").setFinding(f1);
        proj03measure01.getDiagnostic("Property 01 measure diagnostic02").setFinding(f2);
        proj03measure01.getDiagnostic("Property 01 measure diagnostic02").setFinding(f3);
        proj03measure02.getDiagnostic("Property 02 measure diagnostic01").setFinding(f1);
        proj03measure02.getDiagnostic("Property 02 measure diagnostic02").setFinding(f1);
        proj03measure02.getDiagnostic("Property 02 measure diagnostic02").setFinding(f2);
        proj03measure02.getDiagnostic("Property 02 measure diagnostic02").setFinding(f3);


        projects.put(project01.getName(), project01);
        projects.put(project02.getName(), project02);
        projects.put(project03.getName(), project03);

        // Run method
        List<Project> projectsList = new ArrayList<>(projects.values());
        Path generatedMatrix = Benchmarker.createProjectMeasureMatrix(projectsList, analysisResultsAsOutput);

        // Get reader for results
        FileReader fr = new FileReader(generatedMatrix.toFile());
        CSVReader reader = new CSVReader(fr);
        String[] header = reader.readNext();
        String[] row1 = reader.readNext();
        String[] row2 = reader.readNext();
        String[] row3 = reader.readNext();

        String projectAName = row1[0];
        String projectBName = row2[0];
        String projectCName = row3[0];

        String measure01Name = header[1];
        String measure02Name = header[2];

        // Assert results
        Assert.assertEquals(0.1111111, proj01measure01.getValue((double) project01.getLinesOfCode()), 0.000001);
        Assert.assertEquals(0.1111111, proj01measure02.getValue((double) project01.getLinesOfCode()), 0.000001);
        Assert.assertEquals(0.3333333, proj02measure01.getValue((double) project02.getLinesOfCode()), 0.000001);
        Assert.assertEquals(0.3333333, proj02measure02.getValue((double) project02.getLinesOfCode()), 0.000001);
        Assert.assertEquals(0.4444444, proj03measure01.getValue((double) project03.getLinesOfCode()), 0.000001);
        Assert.assertEquals(0.4444444, proj03measure02.getValue((double) project03.getLinesOfCode()), 0.000001);

        Assert.assertEquals(4, (int)reader.getLinesRead());
        Assert.assertTrue(header[0].equalsIgnoreCase("Project_Name"));

        Assert.assertTrue(row1[0].startsWith("Project"));
        Assert.assertTrue(row1[1].equalsIgnoreCase(String.valueOf(projects.get(projectAName).getQualityModel().getMeasure(measure01Name).getValue((double)linesOfCode))));
        Assert.assertTrue(row1[2].equalsIgnoreCase(String.valueOf(projects.get(projectAName).getQualityModel().getMeasure(measure02Name).getValue((double)linesOfCode))));

        Assert.assertTrue(row2[0].startsWith("Project"));
        Assert.assertTrue(row2[1].equalsIgnoreCase(String.valueOf(projects.get(projectBName).getQualityModel().getMeasure(measure01Name).getValue((double)linesOfCode))));
        Assert.assertTrue(row2[2].equalsIgnoreCase(String.valueOf(projects.get(projectBName).getQualityModel().getMeasure(measure02Name).getValue((double)linesOfCode))));

        Assert.assertTrue(row3[0].startsWith("Project"));
        Assert.assertTrue(row3[1].equalsIgnoreCase(String.valueOf(projects.get(projectCName).getQualityModel().getMeasure(measure01Name).getValue((double)linesOfCode))));
        Assert.assertTrue(row3[2].equalsIgnoreCase(String.valueOf(projects.get(projectCName).getQualityModel().getMeasure(measure02Name).getValue((double)linesOfCode))));

        reader.close();
        fr.close();
        FileUtils.deleteDirectory(analysisResultsAsOutput.getParent().toFile());
    }

    @Test
    public void testDeriveThresholds() {
        // TODO: write integration-like test
    }

    @Test
    public void testRThresholdRunnerMapper() {
        Map<String, Double[]> thresholds = Benchmarker.rThresholdRunnerMapper(TestHelper.OUTPUT, this.analysisResultsAsInput);

        Assert.assertEquals(3, thresholds.size());

        Assert.assertTrue(thresholds.containsKey("Property 01 measure"));
        Assert.assertTrue(thresholds.containsKey("Property 02 measure"));
        Assert.assertTrue(thresholds.containsKey("Property 03 measure"));

        Assert.assertArrayEquals(thresholds.get("Property 01 measure"), new Double[]{0.01, 0.019, 0.022});
        Assert.assertArrayEquals(thresholds.get("Property 02 measure"), new Double[]{0.05, 0.068, 0.07});
        Assert.assertArrayEquals(thresholds.get("Property 03 measure"), new Double[]{0.091, 0.093, 0.099});
    }
}
