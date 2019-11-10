package qatch.calibration;

import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class WeighterTests {

    @Test
    public void testElicitateWeights() {

        // Setup
        Path matrices = Paths.get("src/test/resources/comparison_matrices/multiple");
        Path tempResults = Paths.get("src/test/out/weighter");

        // Run method
        Set<WeightResult> result = Weighter.elicitateWeights(matrices, tempResults);

        // Assert expected values


        System.out.println("...");
    }

    @Test
    public void testParseNameOrder() {
        Path matrices = Paths.get("src/test/resources/comparison_matrices/multiple");
        Map<String, ArrayList<String>> result = Weighter.parseNameOrder(matrices);

        Assert.assertEquals(3, result.size());

        Assert.assertTrue(result.containsKey("Design"));
        Assert.assertTrue(result.containsKey("Security"));
        Assert.assertTrue(result.containsKey("TQI"));

        Assert.assertEquals(Arrays.asList("Property 01", "Property 02", "Property 03"), result.get("Design"));
        Assert.assertEquals(Arrays.asList("Property 01", "Property 02", "Property 03"), result.get("Security"));
        Assert.assertEquals(Arrays.asList("Design", "Mobility", "Portability", "Security"), result.get("TQI"));
    }
}
