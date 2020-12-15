package pique.runnable;

import org.junit.Assert;
import org.junit.Test;
import pique.analysis.ITool;
import pique.model.*;
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

        QualityModelImport qmImport = new QualityModelImport(qmFilePath);
        QualityModel qmDescription = qmImport.importQualityModel();

        // The runner method under test: derive a quality model
        QualityModel qualityModel = QualityModelDeriver.deriveModel(qmDescription, tools, locTool, benchmarkRepo,
                projectRootFlag);

        // Assert the derived model, now with weights and thresholds, has the expected form
        // TQI
        Tqi tqi = qualityModel.getTqi();
        Assert.assertEquals("Total Quality", tqi.getName());

        Assert.assertEquals(2, tqi.getChildren().size());
        Assert.assertEquals(2, tqi.getWeights().size());

        Assert.assertTrue(tqi.getChildren().containsKey("QualityAspect 01"));
        Assert.assertTrue(tqi.getChildren().containsKey("QualityAspect 02"));
        Assert.assertTrue(tqi.getWeights().containsKey("QualityAspect 01"));
        Assert.assertTrue(tqi.getWeights().containsKey("QualityAspect 02"));

        Assert.assertEquals(0.5, tqi.getWeight("QualityAspect 01"), 0.0);
        Assert.assertEquals(0.5, tqi.getWeight("QualityAspect 02"), 0.0);

        // Quality Aspects
        ModelNode qa01 = qualityModel.getQualityAspect("QualityAspect 01");
        Assert.assertEquals("QualityAspect 01", qa01.getName());

        Assert.assertEquals(2, qa01.getChildren().size());
        Assert.assertEquals(2, qa01.getWeights().size());

        Assert.assertTrue(qa01.getChildren().containsKey("ProductFactor 01"));
        Assert.assertTrue(qa01.getChildren().containsKey("ProductFactor 02"));
        Assert.assertTrue(qa01.getWeights().containsKey("ProductFactor 01"));
        Assert.assertTrue(qa01.getWeights().containsKey("ProductFactor 02"));

        Assert.assertEquals(0.5, qa01.getWeight("ProductFactor 01"), 0.0);
        Assert.assertEquals(0.5, qa01.getWeight("ProductFactor 02"), 0.0);

        ModelNode qa02 = qualityModel.getQualityAspect("QualityAspect 02");
        Assert.assertEquals("QualityAspect 02", qa02.getName());

        Assert.assertEquals(2, qa02.getChildren().size());
        Assert.assertEquals(2, qa02.getWeights().size());

        Assert.assertTrue(qa02.getChildren().containsKey("ProductFactor 01"));
        Assert.assertTrue(qa02.getChildren().containsKey("ProductFactor 02"));
        Assert.assertTrue(qa02.getWeights().containsKey("ProductFactor 01"));
        Assert.assertTrue(qa02.getWeights().containsKey("ProductFactor 02"));

        Assert.assertEquals(0.5, qa02.getWeight("ProductFactor 01"), 0.0);
        Assert.assertEquals(0.5, qa02.getWeight("ProductFactor 02"), 0.0);

        // Product Factors
        ModelNode pf01 = qa01.getChildByName("ProductFactor 01");
        Assert.assertSame(pf01, qa02.getChildByName("ProductFactor 01"));
        Assert.assertEquals("ProductFactor 01", pf01.getName());
        Assert.assertEquals(1, pf01.getChildren().size());

        ModelNode pf02 = qa01.getChildByName("ProductFactor 02");
        Assert.assertSame(pf02, qa02.getChildByName("ProductFactor 02"));
        Assert.assertEquals("ProductFactor 02", pf02.getName());
        Assert.assertEquals(1, pf02.getChildren().size());

        // Measures
        Measure measure01 = (Measure)pf01.getChildByName("Measure 01");
        Assert.assertEquals("Measure 01", measure01.getName());

        Assert.assertFalse(measure01.isPositive());

        Assert.assertEquals(2, measure01.getThresholds().length);
        Assert.assertEquals(0.02, measure01.getThresholds()[0], 0.0);
        Assert.assertEquals(0.06, measure01.getThresholds()[1], 0.0);

        Assert.assertEquals(2, measure01.getChildren().size());
        Assert.assertTrue(measure01.getChildren().containsKey("TST0011"));
        Assert.assertTrue(measure01.getChildren().containsKey("TST0012"));

        Measure measure02 = (Measure)pf02.getChildByName("Measure 02");
        Assert.assertEquals("Measure 02", measure02.getName());

        Assert.assertFalse(measure02.isPositive());

        Assert.assertEquals(2, measure02.getThresholds().length);
        Assert.assertEquals(0.00, measure02.getThresholds()[0], 0.0);
        Assert.assertEquals(0.06, measure02.getThresholds()[1], 0.0);

        Assert.assertEquals(2, measure02.getChildren().size());
        Assert.assertTrue(measure02.getChildren().containsKey("TST0021"));
        Assert.assertTrue(measure02.getChildren().containsKey("TST0022"));

        // Diagnostics
        Diagnostic diagnostic11 = (Diagnostic)measure01.getChildByName("TST0011");
        Assert.assertEquals("TST0011", diagnostic11.getName());
        Assert.assertEquals("Test tool", diagnostic11.getToolName());

        Diagnostic diagnostic12 = (Diagnostic)measure01.getChildByName("TST0012");
        Assert.assertEquals("TST0012", diagnostic12.getName());
        Assert.assertEquals("Test tool", diagnostic12.getToolName());

        Diagnostic diagnostic21 = (Diagnostic)measure02.getChildByName("TST0021");
        Assert.assertEquals("TST0021", diagnostic21.getName());
        Assert.assertEquals("Test tool", diagnostic21.getToolName());

        Diagnostic diagnostic22 = (Diagnostic)measure02.getChildByName("TST0022");
        Assert.assertEquals("TST0022", diagnostic22.getName());
        Assert.assertEquals("Test tool", diagnostic22.getToolName());
    }

}
