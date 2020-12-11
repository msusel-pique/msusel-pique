package pique.model;

import org.junit.Assert;
import org.junit.Test;
import pique.evaluation.Project;
import pique.utility.Builder;

/**
 * Test abstract {@link ModelNode} class using a concrete representation of it.
 */
public class ModelNodeTests {

    //region Construction

    /**
     * Verify a simple Finding object instances correctly using a Builder
     */
    @Test
    public void testFindingConstruction() {
        Finding node = Builder.buildFinding("file/path", 1, 1);
        Assert.assertSame(node.getClass(), Finding.class);
    }

    /**
     * Verify a simple Diagnostic object instances correctly using a Builder
     */
    @Test
    public void testDiagnosticConstruction() {
        Diagnostic node = Builder.buildDiagnostic("Diagnostic ID");
        Assert.assertSame(node.getClass(), Diagnostic.class);
    }

    /**
     * Verify a simple Measure object instances correctly using a Builder
     */
    @Test
    public void testMeasureConstruction() {
        Measure node = Builder.buildMeasure("Measure Name");
        Assert.assertSame(node.getClass(), Measure.class);
    }

    /**
     * Verify a simple Product Factor object instances correctly using a Builder
     */
    @Test
    public void testProductFactorConstruction() {
        ProductFactor node = Builder.buildProductFactor("Product Factor Name");
        Assert.assertSame(node.getClass(), ProductFactor.class);
    }

    /**
     * Verify a simple Quality Aspect object instances correctly using a Builder
     */
    @Test
    public void testQualityAspectConstruction() {
        QualityAspect node = Builder.buildQualityAspect("Quality Aspect Name");
        Assert.assertSame(node.getClass(), QualityAspect.class);
    }

    /**
     * Verify a simple TQI object instances correctly using a Builder
     */
    @Test
    public void testTQIConstruction() {
        Tqi node = Builder.buildTqi("TQI Name");
        Assert.assertSame(node.getClass(), Tqi.class);
    }

    /**
     * Verify a simple Quality Model object instances correctly using a Builder
     */
    @Test
    public void testQualityModelConstruction() {
        QualityModel node = Builder.buildQualityModel("QM Name");
        Assert.assertSame(node.getClass(), QualityModel.class);
    }

    /**
     * Verify a simple Project object instances correctly using a Builder
     */
    @Test
    public void testProejctConstruction() {
        Project node = Builder.buildProject("Project Name");
        Assert.assertSame(node.getClass(), Project.class);
    }

    //endregion
}
