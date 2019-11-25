package qatch.model;

import org.junit.Assert;
import org.junit.Test;
import qatch.TestHelper;
import qatch.analysis.Finding;

public class TqiTests {

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

        Characteristic c1 = TestHelper.makeCharacteristic("Characteristic 01");
        Characteristic c2 = TestHelper.makeCharacteristic("Characteristic 02");
        c1.setProperty(p1);
        c1.setProperty(p2);
        c2.setProperty(p1);
        c2.setProperty(p2);

        Tqi tqi = TestHelper.makeTqi("Test TQI");
        tqi.setCharacteristic(c1);
        tqi.setCharacteristic(c2);

        Assert.assertEquals(0.7, tqi.getValue(loc), 0);
    }

}
