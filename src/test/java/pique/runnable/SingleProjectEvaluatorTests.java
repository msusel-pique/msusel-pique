package pique.runnable;

import org.junit.Test;
import pique.analysis.ITool;
import pique.model.QualityModel;
import pique.model.QualityModelExport;
import pique.utility.MockedITool;
import pique.utility.MockedLocTool;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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
        QualityModel qmDescription = new QualityModel(qmFilePath);
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

        // PICKUP: investigate QM JVM object after assessment
        throw new NotImplementedException();
    }
}
