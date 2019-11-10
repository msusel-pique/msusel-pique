package qatch.calibration;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;

/**
 * Utility class responsible for deriving the weights (TQI-Characteristics layer and Characteristics-Properties layer)
 * described by hand-entered comparison matrices as part of the analytical hierarchy process.
 *
 * It should ensure the hand-entered matrices match TQI, characteristic, and properties nodes
 * described in the quality model and the data format in the matrices is valid.
 *
 * The class should provide functionality to return a mapping of weights over the two layers given
 * a quality model description and comparison matrices as input by running R scripts.
 */
public class Weighter {

    // Methods

    public static Set<WeightResult> elicitateWeights(Path comparisonMatricesDirectory, Path tempResultsDirectory) {

        // Precondition checks
        if (!comparisonMatricesDirectory.toFile().isDirectory()) {
            throw new RuntimeException("Parameter comparisonMatricesDirectory must be a directory");
        }
        if (Objects.requireNonNull(comparisonMatricesDirectory.toFile().listFiles()).length < 1) {
            throw new RuntimeException("At least one file must exist in comparisonMatricesDirectory");
        }

        // Create directory for temporary generated file results if not yet exists
        tempResultsDirectory.toFile().mkdirs();

        // Run R script



        throw new NotImplementedException();
    }
}
