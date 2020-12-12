package pique.evaluation;

import org.junit.Assert;
import org.junit.Test;
import pique.model.*;
import pique.utility.Builder;

/**
 * Test the default evaluation at each level of a basic quality model
 * Note: These tests make assumption of state based off of the constructions done by the {@link Builder} class.
 */
public class DefaultEvaluatorTests {

    @Test
    public void testDefaultFindingEvaluator() {
        Finding node = Builder.buildFinding("file/path", 22, 3);
        double value = node.getValue();

        Assert.assertEquals(3, value, 0.0);
    }

    @Test
    public void testDefaultDiagnosticEvaluator() {
        Diagnostic node = Builder.buildDiagnostic("Diagnostic 01");
        double value = node.getValue();

        Assert.assertEquals(5, value, 0.0);
    }

    @Test
    public void testDefaultMeasureEvaluator() {
        Measure node = Builder.buildMeasure("Measure 01");
        double value = node.getValue();

        Assert.assertEquals(0.9, value, 0.0);
    }

    @Test
    public void testDefaultProductFactorEvaluator() {
        ProductFactor node = Builder.buildProductFactor("Product Factor 01");
        double value = node.getValue();

        Assert.assertEquals(0.9, value, 0.0);
    }

    @Test
    public void testDefaultQualityAspectEvaluator() {
        QualityAspect node = Builder.buildQualityAspect("Quality Aspect 01");
        double value = node.getValue();

        Assert.assertEquals(0.9, value, 0.0);
    }

    @Test
    public void testDefaultTqiEvaluator() {
        Tqi node = Builder.buildTqi("TQI 01");
        double value = node.getValue();

        Assert.assertEquals(0.9, value, 0.0);
    }
}
