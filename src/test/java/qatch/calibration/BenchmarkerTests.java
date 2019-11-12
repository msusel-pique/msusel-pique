package qatch.calibration;

import com.opencsv.CSVReader;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import qatch.TestHelper;
import qatch.analysis.ITool;
import qatch.analysis.IToolLOC;
import qatch.evaluation.Project;
import qatch.model.QualityModel;

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
        Project project01 = TestHelper.makeProject("Project 01");
        Project project02 = TestHelper.makeProject("Project 02");
        Project project03 = TestHelper.makeProject("Project 03");
        HashMap<String, Project> projects = new HashMap<>();

        project01.getMeasure("Property 01 measure").setNormalizedValue(0.11);
        project01.getMeasure("Property 02 measure").setNormalizedValue(0.12);
        project02.getMeasure("Property 01 measure").setNormalizedValue(0.21);
        project02.getMeasure("Property 02 measure").setNormalizedValue(0.22);
        project03.getMeasure("Property 01 measure").setNormalizedValue(0.31);
        project03.getMeasure("Property 02 measure").setNormalizedValue(0.32);

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
        Assert.assertEquals(4, (int)reader.getLinesRead());

        Assert.assertTrue(header[0].equalsIgnoreCase("Project_Name"));

        Assert.assertTrue(row1[0].startsWith("Project"));
        Assert.assertTrue(row1[1].equalsIgnoreCase(String.valueOf(projects.get(projectAName).getMeasure(measure01Name).getNormalizedValue())));
        Assert.assertTrue(row1[2].equalsIgnoreCase(String.valueOf(projects.get(projectAName).getMeasure(measure02Name).getNormalizedValue())));

        Assert.assertTrue(row2[0].startsWith("Project"));
        Assert.assertTrue(row2[1].equalsIgnoreCase(String.valueOf(projects.get(projectBName).getMeasure(measure01Name).getNormalizedValue())));
        Assert.assertTrue(row2[2].equalsIgnoreCase(String.valueOf(projects.get(projectBName).getMeasure(measure02Name).getNormalizedValue())));

        Assert.assertTrue(row3[0].startsWith("Project"));
        Assert.assertTrue(row3[1].equalsIgnoreCase(String.valueOf(projects.get(projectCName).getMeasure(measure01Name).getNormalizedValue())));
        Assert.assertTrue(row3[2].equalsIgnoreCase(String.valueOf(projects.get(projectCName).getMeasure(measure02Name).getNormalizedValue())));

        reader.close();
        fr.close();
        FileUtils.deleteDirectory(analysisResultsAsOutput.getParent().toFile());
    }

    @Test
    public void testDeriveThresholds() {

        // Initialize mock tools
        IToolLOC fakeLocTool = TestHelper.makeIToolLoc();
        ITool fakeTool = TestHelper.makeITool();
        Map<String, ITool> tools = new HashMap<String, ITool>() {{ put(fakeTool.getName(), fakeTool); }};

        // Create benchmarker and run process
        Map<String, Double[]> result = Benchmarker.deriveThresholds(
                this.benchmarkRepo, new QualityModel(this.qmDescription),
                fakeLocTool, tools,
                ".txt",
                analysisResultsAsOutput, this.rThresholdsOutput);

        // Assert results
        Assert.assertTrue(result.containsKey("Measure 01"));
        Assert.assertTrue(result.containsKey("Measure 02"));

        Double[] m1Values = result.get("Measure 01");
        Double[] m2Values = result.get("Measure 02");

        Assert.assertEquals(0.004, m1Values[0], 0);
        Assert.assertEquals(0.004, m1Values[1], 0);
        Assert.assertEquals(0.004, m1Values[2], 0);

        Assert.assertEquals(0.006, m2Values[0], 0);
        Assert.assertEquals(0.006, m2Values[1], 0);
        Assert.assertEquals(0.006, m2Values[2], 0);
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
