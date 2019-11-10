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
        Set<WeightResult> result = Weighter.elicitateWeights(matrices, tempResults);

        // Run method
        RInvoker.executeRScript(RInvoker.Script.AHP, matrices, tempResults);

        System.out.println("...");
    }
}
