package qatch.calibration;

import com.opencsv.CSVReader;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import qatch.TestHelper;
import qatch.analysis.Diagnostic;
import qatch.analysis.ITool;
import qatch.analysis.IToolLOC;
import qatch.analysis.Measure;
import qatch.evaluation.Project;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class BenchmarkerTests {

    private Path analysisResultsAsInput = Paths.get("src/test/resources/benchmark_results/benchmark_data.csv");
    private Path analysisResultsAsOutput = Paths.get("src/test/out/benchmark_results/benchmark_data.csv");
    private Path benchmarkRepo = Paths.get("src/test/resources/benchmark_repository");
    private Path qmDescription = Paths.get("src/test/resources/quality_models/qualityModel_test_description.json");


    @Test
    public void testCreateProjectMeasureMatrix() throws IOException {

        // Set-up
        Project project01 = TestHelper.makeProject("Project 01");
        Project project02 = TestHelper.makeProject("Project 02");
        Project project03 = TestHelper.makeProject("Project 03");
        HashMap<String, Project> projects = new HashMap<>();
        Benchmarker benchmarker = new Benchmarker();

        project01.getMeasure("Property 01 measure").setNormalizedValue(0.11);
        project01.getMeasure("Property 02 measure").setNormalizedValue(0.12);
        project02.getMeasure("Property 01 measure").setNormalizedValue(0.21);
        project02.getMeasure("Property 02 measure").setNormalizedValue(0.22);
        project03.getMeasure("Property 01 measure").setNormalizedValue(0.31);
        project03.getMeasure("Property 02 measure").setNormalizedValue(0.32);

        projects.put(project01.getName(), project01);
        projects.put(project02.getName(), project02);
        projects.put(project03.getName(), project03);

        benchmarker.setAnalysisResults(analysisResultsAsOutput);

        // Run method
        List<Project> projectsList = new ArrayList<>(projects.values());
        Path generatedMatrix = benchmarker.createProjectMeasureMatrix(projectsList);

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
    public void testAnalyze() {

        // Initialize mock tools
        IToolLOC fakeLocTool = new IToolLOC() {
            @Override
            public Integer analyze(Path projectLocation) {
                return 1000;
            }
        };
        ITool fakeTool = new ITool() {
            @Override
            public Path analyze(Path projectLocation) {
                return Paths.get("src/test/resources/tool_results/faketool_output.xml");
            }

            @Override
            public Map<String, Measure> applyFindings(Map<String, Measure> measures, Map<String, Diagnostic> diagnosticFindings) {
                return null;
            }

            @Override
            public Map<String, Measure> parseConfig(Path toolConfig) {
                return null;
            }

            @Override
            public Map<String, Diagnostic> parseAnalysis(Path toolResults) {
                Map<String, Diagnostic> diagnostics = new HashMap<>();
                diagnostics.put("TST0001", TestHelper.makeDiagnostic("TST0001"));
                diagnostics.put("TST0002", TestHelper.makeDiagnostic("TST0002"));
                diagnostics.put("TST0003", TestHelper.makeDiagnostic("TST0003"));
                diagnostics.put("TST0004", TestHelper.makeDiagnostic("TST0004"));
                diagnostics.put("TST0005", TestHelper.makeDiagnostic("TST0005"));
                return diagnostics;
            }

            @Override
            public Path getConfig() {
                return null;
            }

            @Override
            public String getName() {
                return "Fake Tool";
            }
        };
        Set<ITool> tools = new HashSet<>(Collections.singletonList(fakeTool));

        // Create benchmarker and run process
        Benchmarker benchmarker = new Benchmarker(benchmarkRepo, qmDescription, fakeLocTool, tools);
        benchmarker.analyze(".txt");
    }

    @Test
    public void testGenerateThresholds() {
        Benchmarker benchmarker = new Benchmarker();

        benchmarker.setAnalysisResults(this.analysisResultsAsInput);
        Map<String, Double[]> thresholds = benchmarker.generateThresholds(TestHelper.OUTPUT);

        Assert.assertEquals(3, thresholds.size());

        Assert.assertTrue(thresholds.containsKey("Property 01"));
        Assert.assertTrue(thresholds.containsKey("Property 02"));
        Assert.assertTrue(thresholds.containsKey("Property 03"));

        Assert.assertArrayEquals(thresholds.get("Property 01"), new Double[]{0.01, 0.019, 0.022});
        Assert.assertArrayEquals(thresholds.get("Property 02"), new Double[]{0.05, 0.068, 0.07});
        Assert.assertArrayEquals(thresholds.get("Property 03"), new Double[]{0.091, 0.093, 0.099});
    }
}
