package pique.model;

import org.junit.Assert;
import org.junit.Test;
import pique.calibration.NaiveBenchmarker;
import pique.calibration.NaiveWeighter;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Test high level functions found in the QualityModel class such as model improt from a text file.
 */
public class QualityModelTests {

    @Test
    public void testImportQualityModel() {

        Path qmFilePath = Paths.get("src/test/resources/quality_models/qualityModel_minimal_description.json");
        QualityModel qmDescription = new QualityModel(qmFilePath);

        // Top level properties
        Assert.assertEquals(NaiveBenchmarker.class.getCanonicalName(),
                qmDescription.getBenchmarker().getClass().getCanonicalName());

        Assert.assertEquals(
                NaiveWeighter.class.getCanonicalName(), qmDescription.getWeighter().getClass().getCanonicalName());

        // Tqi
        Tqi tqi = qmDescription.getTqi();
        Assert.assertEquals("Total Quality", tqi.getName());
        Assert.assertEquals(2, tqi.getNumChildren());

        // Quality Aspects
        QualityAspect qualityAspect1 = (QualityAspect)tqi.getChildByName("QualityAspect 01");
        Assert.assertEquals("QualityAspect 01", qualityAspect1.getName());
        Assert.assertEquals(2, qualityAspect1.getNumChildren());

        QualityAspect qualityAspect2 = (QualityAspect)tqi.getChildByName("QualityAspect 02");
        Assert.assertEquals("QualityAspect 02", qualityAspect2.getName());
        Assert.assertEquals(2, qualityAspect2.getNumChildren());

        // Product Factors
        ProductFactor productFactor1 = (ProductFactor)qualityAspect1.getChildByName("ProductFactor 01");
        Assert.assertEquals("ProductFactor 01", productFactor1.getName());
        Assert.assertEquals(1, productFactor1.getNumChildren());

        ProductFactor productFactor2 = (ProductFactor)qualityAspect1.getChildByName("ProductFactor 02");
        Assert.assertEquals("ProductFactor 02", productFactor2.getName());
        Assert.assertEquals(1, productFactor2.getNumChildren());

        Assert.assertEquals(productFactor1, qualityAspect2.getChildByName("ProductFactor 01"));
        Assert.assertEquals(productFactor2, qualityAspect2.getChildByName("ProductFactor 02"));

        // Measures
        Measure measure1 = (Measure)productFactor1.getChildByName("Measure 01");
        Assert.assertEquals("Measure 01", measure1.getName());
        Assert.assertFalse(measure1.isPositive());
        Assert.assertEquals(2, measure1.getNumChildren());

        Measure measure2 = (Measure)productFactor2.getChildByName("Measure 02");
        Assert.assertEquals("Measure 02", measure2.getName());
        Assert.assertFalse(measure2.isPositive());
        Assert.assertEquals(2, measure2.getNumChildren());

        // Diagnostics
        Diagnostic diagnostic11 = (Diagnostic)measure1.getChildByName("TST0011");
        Assert.assertEquals("TST0011", diagnostic11.getName());
        Assert.assertEquals(0, diagnostic11.getNumChildren());

        Diagnostic diagnostic12 = (Diagnostic)measure1.getChildByName("TST0012");
        Assert.assertEquals("TST0012", diagnostic12.getName());
        Assert.assertEquals(0, diagnostic12.getNumChildren());

        Diagnostic diagnostic21 = (Diagnostic)measure2.getChildByName("TST0021");
        Assert.assertEquals("TST0021", diagnostic21.getName());
        Assert.assertEquals(0, diagnostic21.getNumChildren());

        Diagnostic diagnostic22 = (Diagnostic)measure2.getChildByName("TST0022");
        Assert.assertEquals("TST0022", diagnostic22.getName());
        Assert.assertEquals(0, diagnostic22.getNumChildren());
    }
}
