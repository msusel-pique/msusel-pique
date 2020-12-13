package pique.runnable;

import org.junit.Test;
import pique.analysis.ITool;
import pique.model.QualityModel;
import pique.utility.MockedITool;
import pique.utility.MockedLocTool;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Integration tests of model derivation processes
 */
public class QualityModelDeriverTests {

    /**
     * Run the main model derivation method using mocked tool results
     */
    @Test
    public void testDeriveModel() {

        Path qmFilePath = Paths.get("src/test/resources/quality_models/qualityModel_minimal_description.json");

        // Initialize objects
        String projectRootFlag = ".txt";
        Path benchmarkRepo = Paths.get("src/test/resources/benchmark_repository");
        ITool mockedTool = new MockedITool();
        Set<ITool> tools = Stream.of(mockedTool).collect(Collectors.toSet());
        ITool locTool = new MockedLocTool();

        QualityModel qmDescription = new QualityModel(qmFilePath);

        // The runner method under test: derive a quality model
        QualityModel qualityModel = QualityModelDeriver.deriveModel(qmDescription, tools, locTool, benchmarkRepo,
                projectRootFlag);

        throw new NotImplementedException();
    }

}
