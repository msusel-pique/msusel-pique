package qatch.runnable;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import qatch.TestHelper;
import qatch.evaluation.Project;
import qatch.model.QualityModel;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SingleProjectEvaluatorTests {

    private SingleProjectEvaluator spe;
    private Path PROJECT_DIR = Paths.get("src/test/resources/FakeProject_01");
    private Path RESULTS_DIR = Paths.get("src/test/out/SingleProjEval");
    private Path QM_LOCATION = Paths.get("src/test/resources/quality_models/qualityModel_test.json");
    private Path TOOL_RESULTS = Paths.get("src/test/resources/tool_results");
    private Path TEST_OUT = Paths.get("src/test/out");


    @Before
    public void cleanBefore() throws IOException {
        TestHelper.cleanTestOutput();
    }

    @Before
    public void initClass() {
        spe = new SingleProjectEvaluator();
    }

    @After
    public void cleanAfter() throws IOException {
        TestHelper.cleanTestOutput();
    }

    @Test
    public void testAggregateNormalize() {
        // TODO: test might not be worth writing due to interface call
        Assert.assertTrue(true);
    }

//    @Test
    public void testEvaluate() {
        Project p = TestHelper.makeProject("Test Project");
        // temp fix to avoid QM clone problem
//        p.getCharacteristics_depreicated().removeCharacteristic(0);
//        p.getCharacteristics_depreicated().removeCharacteristic(0);

        QualityModel qm = new QualityModel(QM_LOCATION);

        // TODO: add more edge cases
//        p.getProperties_depreicated().get(0).getMeasure().setNormalizedValue(0.90);
//        p.getProperties_depreicated().get(1).getMeasure().setNormalizedValue(0.10);

//        spe.evaluate_deprecated(p, qm);

//        Assert.assertEquals(0.099999, p.getProperties_depreicated().get(0).getValue(),0.00001);
//        Assert.assertEquals(0.9, p.getProperties_depreicated().get(1).getValue(),0.00001);

//        Assert.assertEquals(0.42, p.getCharacteristics_depreicated().get(0).getValue(), 0.00001);
//        Assert.assertEquals(0.50, p.getCharacteristics_depreicated().get(1).getValue(), 0.00001);

        Assert.assertEquals(0.436, p.getTqi().getValue(),0.00001);
    }


    @Test
    public void testInitialize() {
        try { spe.initialize(PROJECT_DIR, RESULTS_DIR, QM_LOCATION); }
        catch (IllegalArgumentException e) { Assert.fail(e.getMessage()); }

        try {
            spe.initialize(
                    Paths.get("src/test/resources/IDONTEXIST"),
                    Paths.get("src/test/out/SingleProjEval"),
                    QM_LOCATION
            );
        } catch (IllegalArgumentException ignored) {  }
    }

    @Test
    public void testRunEvaluator() {

        SingleProjectEvaluator spe = new SingleProjectEvaluator();
        spe.runEvaluator(PROJECT_DIR, RESULTS_DIR, QM_LOCATION, TestHelper.makeITool(), TestHelper.makeIToolLoc());

        System.out.println("...");

        // TODO PICKUP: write test run evaluator on mock objects
    }
}
