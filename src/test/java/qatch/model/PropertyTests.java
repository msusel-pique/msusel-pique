package qatch.model;

import org.junit.Assert;
import org.junit.Test;
import qatch.TestHelper;
import qatch.analysis.Finding;

public class PropertyTests {

    @Test
    public void testEvaluate() {

        Double loc = 15.0;

        Finding f1 = TestHelper.makeFinding("file/path", 11, 1);
        Finding f2 = TestHelper.makeFinding("file/path", 22, 1);
        Finding f3 = TestHelper.makeFinding("file/path", 22, 1);

        Property p = TestHelper.makeProperty("Property 01");

        p.getMeasure().getDiagnostic("Property 01 measure diagnostic01").setFinding(f1);
        p.getMeasure().getDiagnostic("Property 01 measure diagnostic02").setFinding(f2);
        p.getMeasure().getDiagnostic("Property 01 measure diagnostic02").setFinding(f3);

        Assert.assertEquals(.5, p.getValue(loc), 0);
    }

}
