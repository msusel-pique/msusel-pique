package qatch.calibration;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
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
}
