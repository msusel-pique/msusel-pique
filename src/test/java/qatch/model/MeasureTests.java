package qatch.model;

import org.junit.Assert;
import org.junit.Test;
import qatch.TestHelper;
import qatch.analysis.Finding;
import qatch.analysis.Measure;

public class MeasureTests {

    @Test
    public void testEvaluate() {
        Finding f1 = TestHelper.makeFinding("file/path", 11, 1);
        Finding f2 = TestHelper.makeFinding("file/path", 22, 1);
        Finding f3 = TestHelper.makeFinding("file/path", 22, 1);
        Measure m = TestHelper.makeMeasure("Measure 01");

        m.getDiagnostic("Measure 01 diagnostic01").setFinding(f1);
        m.getDiagnostic("Measure 01 diagnostic02").setFinding(f2);
        m.getDiagnostic("Measure 01 diagnostic02").setFinding(f3);

        Assert.assertEquals(3, m.getValue(), 0);
    }

}
