package qatch.model;

import org.junit.Assert;
import org.junit.Test;
import qatch.TestHelper;
import qatch.analysis.Diagnostic;
import qatch.analysis.Measure;

import java.nio.file.Path;
import java.nio.file.Paths;

public class QualityModelTests {

    private Path qmTest = Paths.get("src/test/resources/quality_models/qualityModel_test.json");

    /*
     * Test full quality model clone stack (Diagnostic.clone() -> Measure.clone() -> ... -> Tqi.clone())
     */
    @Test
    public void testCloneStack() {
        QualityModel qmOirginal = TestHelper.makeQualityModel();

        Diagnostic diagnosticOriginal = qmOirginal.getMeasure("Property 01 measure").getDiagnostic("Property 01 measure diagnostic01");
        Diagnostic diagnosticNew = (Diagnostic) diagnosticOriginal.clone();
        Measure measureOriginal = qmOirginal.getMeasure("Property 01 measure");
        Measure measureNew = (Measure) measureOriginal.clone();
        Property propertyOriginal = qmOirginal.getProperty("Property 01");
        Property propertyNew = (Property)propertyOriginal.clone();
        Characteristic characteristicOriginal = qmOirginal.getCharacteristic("Characteristic 01");
        Characteristic characteristicNew = (Characteristic)characteristicOriginal.clone();
        Tqi tqiOriginal = qmOirginal.getTqi();
        Tqi tqiNew = (Tqi)tqiOriginal.clone();
        QualityModel qmNew = qmOirginal.clone();

        Assert.assertNotSame(diagnosticOriginal, diagnosticNew);
        Assert.assertEquals(diagnosticOriginal, diagnosticNew);

        Assert.assertNotSame(measureOriginal, measureNew);
        Assert.assertEquals(measureOriginal, measureNew);

        Assert.assertNotSame(propertyOriginal, propertyNew);
        Assert.assertEquals(propertyOriginal, propertyNew);

        Assert.assertNotSame(characteristicOriginal, characteristicNew);
        Assert.assertEquals(characteristicOriginal, characteristicNew);

        Assert.assertNotSame(tqiOriginal, tqiNew);
        Assert.assertEquals(tqiOriginal, tqiNew);

        Assert.assertNotSame(qmOirginal, qmNew);
        Assert.assertEquals(qmOirginal, qmNew);
    }

    @Test
    public void testImportQualityModel() {
        QualityModel qm = new QualityModel(qmTest);

        Tqi tqi = qm.getTqi();
        Characteristic ch1 = qm.getCharacteristic("Characteristic 01");
        Characteristic ch2 = qm.getCharacteristic("Characteristic 02");
        Property p1 = qm.getProperty("Property 01");
        Property p2 = qm.getProperty("Property 02");
        double[] thresholds01 = new double[] { 0.0, 0.004, 0.02 };
        double[] thresholds02 = new double[] { 0.0, 0.01, 0.02 };
        Measure p1Measure = p1.getMeasure();
        Measure p2Measure = p2.getMeasure();

        Assert.assertEquals("Test QM", qm.getName());

        Assert.assertEquals("Total Quality", tqi.getName());
        Assert.assertEquals(2, tqi.getWeights().size());
        Assert.assertEquals(0.8, tqi.getWeight("Characteristic 01"), 0);
        Assert.assertEquals(0.2, tqi.getWeight("Characteristic 02"), 0);

        Assert.assertEquals("Characteristic 01", ch1.getName());
        Assert.assertEquals("Characteristic 02", ch2.getName());
        Assert.assertEquals(0.6, ch1.getWeight("Property 01"), 0);
        Assert.assertEquals(0.4, ch1.getWeight("Property 02"), 0);
        Assert.assertEquals(0.5, ch2.getWeight("Property 01"), 0);
        Assert.assertEquals(0.5, ch2.getWeight("Property 02"), 0);

        Assert.assertEquals("Property 01", p1.getName());
        Assert.assertEquals("Property 02", p2.getName());
        Assert.assertFalse(p1.isPositive());
        Assert.assertFalse(p2.isPositive());
        Assert.assertEquals(thresholds01[1], p1.getThresholds()[1], 0);
        Assert.assertEquals(thresholds02[1], p2.getThresholds()[1], 0);

        Assert.assertEquals("Measure 01", p1Measure.getName());
        Assert.assertEquals("Measure 02", p2Measure.getName());
        Assert.assertEquals("TST0001", p1Measure.getDiagnostic("TST0001").getName());
        Assert.assertEquals("TST0004", p2Measure.getDiagnostic("TST0004").getName());

        // Assert tree structure pass-by-reference from TQI root node compared to fields
        qm.getTqi().getCharacteristics().values().forEach(tqiCharacteristic -> {
            // Characteristic layer
            Assert.assertEquals(qm.getCharacteristics().get(tqiCharacteristic.getName()), tqiCharacteristic);
            tqiCharacteristic.getProperties().values().forEach(tqiProperty -> {
                // Properties layer
                Assert.assertEquals(qm.getProperties().get(tqiProperty.getName()), tqiProperty);
                Measure qmMeasure = qm.getProperties().get(tqiProperty.getName()).getMeasure();
                // Property's measure
                Assert.assertEquals(qmMeasure, tqiProperty.getMeasure());
                tqiProperty.getMeasure().getDiagnostics().forEach(tqiDiagnostic -> {
                    // Measure's diagnostics
                    Assert.assertEquals(qmMeasure.getDiagnostic(tqiDiagnostic.getName()), tqiDiagnostic);
                });
            });
        });
    }
}
