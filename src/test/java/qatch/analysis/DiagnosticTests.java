package qatch.analysis;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;
import java.util.function.Function;

public class DiagnosticTests {

    private Finding finding01, finding02, finding03;

    @Before
    public void initialize() {
        finding01 = new Finding("A", 1, 1, 1);
        finding02 = new Finding("B", 2, 2, 2);
        finding03 = new Finding("C", 3, 3, 3);
    }


    @Test
    public void testEvaluate_defaultFunction() {
        Diagnostic d = new Diagnostic("id01", "Sample Description", "Sample Tool Name");
        d.setFinding(finding01);
        d.setFinding(finding02);
        d.setFinding(finding03);

        double v = d.getValue();
        Assert.assertEquals(3, v, 0);
    }

    @Test
    public void testEvaluate_customFunction() {
        Function<Set<Finding>, Double> customFunct = (Set<Finding> findings) -> {
            double value = 0.0;
            for (Finding f : findings) {
                value += f.getSeverity();
            }
            return value;
        };

        Diagnostic d = new Diagnostic("id01", "Sample Description", "Sample Tool Name", customFunct);
        d.setFinding(finding01);
        d.setFinding(finding02);
        d.setFinding(finding03);

        double v = d.getValue();
        Assert.assertEquals(6, v, 0);

    }

}
