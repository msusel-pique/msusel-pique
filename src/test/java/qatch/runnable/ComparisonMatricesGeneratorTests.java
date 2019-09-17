package qatch.runnable;

import com.opencsv.CSVReader;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import qatch.TestHelper;
import qatch.model.Characteristic;
import qatch.model.CharacteristicSet;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

public class ComparisonMatricesGeneratorTests {

    @Before
    public void cleanBefore() throws IOException {
        TestHelper.cleanTestOutput();
    }

    @After
    public void cleanAfter() throws IOException {
        TestHelper.cleanTestOutput();
    }


    @Test
    public void testMain() {
        String qmDescription = TestHelper.TEST_RESOURCES.toString() + "/quality_models/qualityModel_test_description.xml";
        String output = TestHelper.OUTPUT.toString();
        String runFuzzy = "false";

        ComparisonMatricesGenerator.main(new String[]{ qmDescription, output, runFuzzy });

        System.out.println("...");
    }

    @Test
    public void testMakeCsvComparisonMatrix() throws IOException {
        String name = "Some Name";
        String[] comparitors = { "Property 1", "Property 2", "Property 3" };
        String defaultChar = "0";
        Path output = TestHelper.OUTPUT;

        Path result = ComparisonMatricesGenerator.makeCsvComparisonMatrix(name, comparitors, defaultChar, output);
        CSVReader reader = new CSVReader(new FileReader(result.toFile()));
        String[] header = reader.readNext();
        String[] row1 = reader.readNext();
        String[] row2 = reader.readNext();
        String[] row3 = reader.readNext();

        Assert.assertEquals(4, (int)reader.getLinesRead());

        Assert.assertTrue(header[0].equalsIgnoreCase("Some Name"));
        Assert.assertTrue(header[1].equalsIgnoreCase("Property 1"));
        Assert.assertTrue(header[2].equalsIgnoreCase("Property 2"));
        Assert.assertTrue(header[3].equalsIgnoreCase("Property 3"));

        Assert.assertTrue(row1[0].equalsIgnoreCase("Property 1"));
        Assert.assertTrue(row1[1].equalsIgnoreCase("0"));

        Assert.assertTrue(row2[0].equalsIgnoreCase("Property 2"));
        Assert.assertTrue(row2[1].equalsIgnoreCase("0"));
        Assert.assertTrue(row2[2].equalsIgnoreCase("0"));

        Assert.assertTrue(row3[0].equalsIgnoreCase("Property 3"));
        Assert.assertTrue(row3[1].equalsIgnoreCase("0"));
        Assert.assertTrue(row3[2].equalsIgnoreCase("0"));
        Assert.assertTrue(row3[3].equalsIgnoreCase("0"));

        reader.close();
    }

    @Test
    public void testSubroutineTQI() throws IOException {

        Characteristic c1 = TestHelper.makeCharacteristic("Characteristic 01");
        Characteristic c2 = TestHelper.makeCharacteristic("Characteristic 02");
        CharacteristicSet cs = TestHelper.makeCharacteristicSet(c1, c2);

        Path output = TestHelper.OUTPUT;
        output.toFile().mkdirs();
        String defaultChar = "0";

        Path tqiFile = ComparisonMatricesGenerator.subroutineTQI(cs, output, defaultChar);

        CSVReader reader = new CSVReader(new FileReader(tqiFile.toFile()));
        String[] header = reader.readNext();
        String[] row1 = reader.readNext();
        String[] row2 = reader.readNext();

        Assert.assertEquals(3, (int)reader.getLinesRead());
        Assert.assertEquals(3, (int)reader.getRecordsRead());

        Assert.assertTrue(header[0].equalsIgnoreCase("tqi"));
        Assert.assertTrue(header[1].equalsIgnoreCase("characteristic 01"));
        Assert.assertTrue(header[2].equalsIgnoreCase("characteristic 02"));

        Assert.assertTrue(row1[0].equalsIgnoreCase("characteristic 01"));
        Assert.assertTrue(row1[1].equalsIgnoreCase("0"));

        Assert.assertTrue(row2[0].equalsIgnoreCase("characteristic 02"));
        Assert.assertTrue(row2[1].equalsIgnoreCase("0"));
        Assert.assertTrue(row2[2].equalsIgnoreCase("0"));

        reader.close();
    }

}
