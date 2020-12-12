package pique.evaluation;

import org.junit.Assert;
import org.junit.Test;
import pique.model.Diagnostic;
import pique.model.Finding;
import pique.model.Measure;
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

        Assert.assertEquals(5, value, 0.0);
    }
}
