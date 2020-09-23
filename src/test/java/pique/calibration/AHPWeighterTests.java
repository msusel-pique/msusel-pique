package pique.calibration;

import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class AHPWeighterTests {

    @Test
    public void testElicitateWeights() {

        // Setup
        Path matrices = Paths.get("src/test/resources/comparison_matrices/multiple");
        Path tempResults = Paths.get("src/test/out/weighter");

        // Run method
        Set<WeightResult> results = new AHPWeighter().elicitateWeights(matrices, tempResults);

        // Assert expected values
        for (WeightResult result : results) {
            switch (result.name) {
                case "TQI":
                    Assert.assertEquals(0.1499, result.weights.get("Design"), 0);
                    Assert.assertEquals(0.0446, result.weights.get("Mobility"), 0);
                    Assert.assertEquals(0.0732, result.weights.get("Portability"), 0);
                    Assert.assertEquals(0.7323, result.weights.get("Security"), 0);
                    break;
                case "Design":
                case "Security":
                    Assert.assertEquals(0.5842, result.weights.get("Property 01"), 0);
                    Assert.assertEquals(0.135, result.weights.get("Property 02"), 0);
                    Assert.assertEquals(0.2808, result.weights.get("Property 03"), 0);
                    break;
                default:
                    Assert.fail("No expected WeightResult node names were found");
                    break;
            }
        }
    }

    @Test
    public void testElicitateWeights_linguistic() {

        // Setup
        Path matrices = Paths.get("src/test/resources/comparison_matrices/linguistic");
        Path tempResults = Paths.get("src/test/out/weighter");

        // Run method
        Set<WeightResult> results = new AHPWeighter().elicitateWeights(matrices, tempResults);

        // Assert expected values
        for (WeightResult result : results) {
            switch (result.name) {
                case "TQI":
                    Assert.assertEquals(0.0658, result.weights.get("Portability"), 0);
                    Assert.assertEquals(0.7171, result.weights.get("Maintainability"), 0);
                    Assert.assertEquals(0.2172, result.weights.get("Security"), 0);
                    break;
                case "Portability":
                    Assert.assertEquals(0.6568, result.weights.get("Modularity"), 0);
                    Assert.assertEquals(0.0625, result.weights.get("Redundancy"), 0);
                    Assert.assertEquals(0.2183, result.weights.get("Documentation"), 0);
                    Assert.assertEquals(0.0625, result.weights.get("Encryption"), 0);
                    break;
                default:
                    Assert.fail("No expected WeightResult node names were found");
                    break;
            }
        }
    }

    @Test
    public void testElicitateWeights_ones() {

        // Setup
        Path matrices = Paths.get("src/test/resources/comparison_matrices/ones");
        Path tempResults = Paths.get("src/test/out/weighter");

        // Run method
        Set<WeightResult> results = new AHPWeighter().elicitateWeights(matrices, tempResults);

        // Assert expected values
        for (WeightResult result : results) {
            switch (result.name) {
                case "Portability":
                    Assert.assertEquals(0.25, result.weights.get("Modularity"), 0);
                    Assert.assertEquals(0.25, result.weights.get("Redundancy"), 0);
                    Assert.assertEquals(0.25, result.weights.get("Documentation"), 0);
                    Assert.assertEquals(0.25, result.weights.get("Encryption"), 0);
                    break;
                default:
                    Assert.fail("No expected WeightResult node names were found");
                    break;
            }
        }
    }

    @Test
    public void testParseNameOrder() {
        Path matrices = Paths.get("src/test/resources/comparison_matrices/multiple");
        Map<String, ArrayList<String>> result = AHPWeighter.parseNameOrder(matrices);

        Assert.assertEquals(3, result.size());

        Assert.assertTrue(result.containsKey("Design"));
        Assert.assertTrue(result.containsKey("Security"));
        Assert.assertTrue(result.containsKey("TQI"));

        Assert.assertEquals(Arrays.asList("Property 01", "Property 02", "Property 03"), result.get("Design"));
        Assert.assertEquals(Arrays.asList("Property 01", "Property 02", "Property 03"), result.get("Security"));
        Assert.assertEquals(Arrays.asList("Design", "Mobility", "Portability", "Security"), result.get("TQI"));
    }
}
