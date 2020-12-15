package pique.runnable;

import org.junit.Assert;
import org.junit.Test;
import pique.analysis.ITool;
import pique.evaluation.Project;
import pique.model.*;
import pique.utility.MockedITool;
import pique.utility.MockedLocTool;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Integration tests for single project evaluation processes
 */
public class SingleProjectEvaluatorTests {

    @Test
    public void testSingleProjectEvaluator() {

        // First, derive a quality model using the deriver
        Path qmFilePath = Paths.get("src/test/resources/quality_models/qualityModel_minimal_description.json");
        String projectRootFlag = ".txt";
        Path benchmarkRepo = Paths.get("src/test/resources/benchmark_repository");
        ITool mockedTool = new MockedITool();
        Set<ITool> tools = Stream.of(mockedTool).collect(Collectors.toSet());
        ITool locTool = new MockedLocTool();

        QualityModelImport qmImport = new QualityModelImport(qmFilePath);
        QualityModel qmDescription = qmImport.importQualityModel();
        QualityModel qualityModel = QualityModelDeriver.deriveModel(qmDescription, tools, locTool, benchmarkRepo,
                projectRootFlag);

        // Export as artifact
        Path outputDirectory = Paths.get("src/test/out");
        String qmDerivedName = "qualityModel_minimal_derived";
        QualityModelExport qmExport = new QualityModelExport(qualityModel);
        qmExport.exportToJson(qmDerivedName, outputDirectory);
        Path qmDerivedOutputFile = Paths.get(outputDirectory.toString(), qmDerivedName + ".json");

        // Now, run project evaluation using the derived model
        Path projectPath = Paths.get("src/test/resources/fake_project");
        Path evaluationResultsPath = Paths.get("src/test/out/single_project_evaluation_results");
        SingleProjectEvaluator evaluator = new SingleProjectEvaluator();
        Path evaluationResults = evaluator.runEvaluator(projectPath, evaluationResultsPath, qmDerivedOutputFile, tools,
                locTool);

        // Get JVM representation of the evaluated project and make assertions
        Project evaluatedProject = evaluator.getEvaluatedProject();

        // Project
        Assert.assertEquals("fake_project", evaluatedProject.getName());
        Assert.assertEquals(100, evaluatedProject.getLinesOfCode());

        // QM Handles
        Tqi tqi = evaluatedProject.getQualityModel().getTqi();
        ModelNode qa01 = tqi.getChildByName("QualityAspect 01");
        ModelNode qa02 = tqi.getChildByName("QualityAspect 02");
        ModelNode pf01 = qa01.getChildByName("ProductFactor 01");
        ModelNode pf02 = qa02.getChildByName("ProductFactor 02");
        ModelNode measure01 = pf01.getChildByName("Measure 01");
        ModelNode measure02 = pf02.getChildByName("Measure 02");
        ModelNode diagnostic11 = measure01.getChildByName("TST0011");
        ModelNode diagnostic12 = measure01.getChildByName("TST0012");
        ModelNode diagnostic21 = measure02.getChildByName("TST0021");
        ModelNode diagnostic22 = measure02.getChildByName("TST0022");

        // Diagnostics
        Assert.assertEquals(1, diagnostic11.getValue(), 0.0001);
        Assert.assertEquals(4, diagnostic12.getValue(), 0.0001);
        Assert.assertEquals(1, diagnostic21.getValue(), 0.0001);
        Assert.assertEquals(0, diagnostic22.getValue(), 0.0001);

        // Measures
        Assert.assertEquals(0.25, measure01.getValue(), 0.0001);
        Assert.assertEquals(0.8333, measure02.getValue(), 0.0001);

        // Product Factors
        Assert.assertEquals(0.25, pf01.getValue(), 0.0001);
        Assert.assertEquals(0.83333, pf02.getValue(), 0.0001);

        // Quality Aspects
        Assert.assertEquals(0.5416, qa01.getValue(), 0.0001);
        Assert.assertEquals(0.5416, qa02.getValue(), 0.0001);

        // TQI
        Assert.assertEquals(0.5416, tqi.getValue(), 0.0001);

    }
}
