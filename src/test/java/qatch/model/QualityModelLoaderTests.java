package qatch.model;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class QualityModelLoaderTests {

    private Path qmTest = Paths.get("src/test/resources/quality_models/qualityModel_test.json");

    @Test
    public void testImportQualityModel() {
        QualityModelLoader qml = new QualityModelLoader(qmTest);
        QualityModel qm = qml.importQualityModel();
        System.out.println("...");
    }

}
