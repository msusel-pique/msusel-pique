package qatch.model;

import org.junit.Assert;
import org.junit.Test;
import qatch.TestHelper;
import qatch.analysis.Diagnostic;
import qatch.analysis.Finding;

public class DiagnosticTests {

    @Test
    public void testEvaluate() {
        Finding f1 = TestHelper.makeFinding("file/path", 11, 1);
        Finding f2 = TestHelper.makeFinding("file/path", 22, 1);
        Finding f3 = TestHelper.makeFinding("file/path", 22, 1);
        Diagnostic d = TestHelper.makeDiagnostic("diag01");

        d.setFinding(f1);
        d.setFinding(f2);
        d.setFinding(f3);

        Assert.assertEquals(3, d.getValue(), 0);
    }
}
