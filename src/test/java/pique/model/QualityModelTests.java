package pique.model;

import org.junit.Assert;
import org.junit.Test;
import pique.analysis.ITool;
import pique.runnable.QualityModelDeriver;
import pique.utility.MockedIToolQmFull;
import pique.utility.MockedIToolQmSimple;
import pique.utility.MockedLocTool;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Test high level functions found in the QualityModel class such as model improt from a text file.
 */
public class QualityModelTests {

    @Test
    public void testGetAllQualityModelNodes() {

        // Initialize the QM using a model description file
        Path qmFilePath = Paths.get("src/test/resources/quality_models/qualityModel_full_description.json");
        QualityModelImport qmImport = new QualityModelImport(qmFilePath);
        QualityModel qmDescription = qmImport.importQualityModel();

        // Run method
        Map<String, ModelNode> allNodes = qmDescription.getAllQualityModelNodes();

        // Assert
        Assert.assertEquals(10, allNodes.size());
    }

    @Test
    public void testGetDiagnostics() {

        // Initialize the QM using a model description file
        Path qmFilePath = Paths.get("src/test/resources/quality_models/qualityModel_full_description.json");
        QualityModelImport qmImport = new QualityModelImport(qmFilePath);
        QualityModel qmDescription = qmImport.importQualityModel();

        // Run method
        Map<String, ModelNode> diagnostics = qmDescription.getDiagnostics();

        // Assert
        Assert.assertEquals(2, diagnostics.size());
    }

    @Test
    public void testGetMeasures() {

        // Initialize the QM using a model description file
        Path qmFilePath = Paths.get("src/test/resources/quality_models/qualityModel_full_description.json");
        QualityModelImport qmImport = new QualityModelImport(qmFilePath);
        QualityModel qmDescription = qmImport.importQualityModel();

        // Run method
        Map<String, ModelNode> measures = qmDescription.getMeasures();

        // Assert
        Assert.assertEquals(1, measures.size());
    }

    @Test
    public void testGetProductFactors() {

        // Initialize the QM using a model description file
        Path qmFilePath = Paths.get("src/test/resources/quality_models/qualityModel_full_description.json");
        QualityModelImport qmImport = new QualityModelImport(qmFilePath);
        QualityModel qmDescription = qmImport.importQualityModel();

        // Run method
        Map<String, ModelNode> productFactors = qmDescription.getProductFactors();

        // Assert
        Assert.assertEquals(2, productFactors.size());
    }

    /**
     * Not a real test (yet) but exports a derived quality model for manual verification
     */
    @Test
    public void testExportQualityModel_Derived() {

        // Run model derivation process
        Path qmFilePath = Paths.get("src/test/resources/quality_models/qualityModel_minimal_description.json");

        String projectRootFlag = ".txt";
        Path benchmarkRepo = Paths.get("src/test/resources/benchmark_repository");
        ITool mockedTool = new MockedIToolQmSimple();
        Set<ITool> tools = Stream.of(mockedTool).collect(Collectors.toSet());
        ITool locTool = new MockedLocTool();

        QualityModelImport qmImport = new QualityModelImport(qmFilePath);
        QualityModel qmDescription = qmImport.importQualityModel();
        QualityModel qualityModel = QualityModelDeriver.deriveModel(qmDescription, tools, locTool, benchmarkRepo,
                projectRootFlag);

        // Export as artifact
        Path outputDirectory = Paths.get("src/test/out");
        QualityModelExport qmExport = new QualityModelExport(qualityModel);
        qmExport.exportToJson("qualityModel_minimal_derived", outputDirectory);
    }
}
