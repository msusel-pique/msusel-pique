package qatch.model;

import org.junit.Assert;
import org.junit.Test;
import qatch.TestHelper;
import qatch.analysis.Finding;

public class CharacteristicTests {

    @Test
    public void testEvaluate() {

        Double loc = 15.0;

        Finding f1 = TestHelper.makeFinding("file/path", 11, 1);
        Finding f2 = TestHelper.makeFinding("file/path", 22, 1);
        Finding f3 = TestHelper.makeFinding("file/path", 22, 1);

        Property p1 = TestHelper.makeProperty("Property 01");
        Property p2 = TestHelper.makeProperty("Property 02");
        p1.getMeasure().getDiagnostic("Property 01 measure diagnostic01").setFinding(f1);
        p1.getMeasure().getDiagnostic("Property 01 measure diagnostic02").setFinding(f2);
        p2.getMeasure().getDiagnostic("Property 02 measure diagnostic01").setFinding(f1);
        p2.getMeasure().getDiagnostic("Property 02 measure diagnostic02").setFinding(f2);
        p2.getMeasure().getDiagnostic("Property 02 measure diagnostic02").setFinding(f3);

        Characteristic c = TestHelper.makeCharacteristic("Characteristic 01");
        c.setProperty(p1);
        c.setProperty(p2);

        Assert.assertEquals(0.7, c.getValue(loc), 0);
    }
}
