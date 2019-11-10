package qatch.calibration;

import com.opencsv.CSVReader;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

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

        // Parse node name ordering for each matrix
        // TODO: eventually easiest to just have R map the names to the weights instead of parsing the files again


        // Run R script
        RInvoker.executeRScript(RInvoker.Script.AHP, comparisonMatricesDirectory, tempResultsDirectory);

        // Transform into WeightResults object
        System.out.println("...");

        // Return set
        throw new NotImplementedException();
    }


    /**
     * Extract the left-to-right order of the comparison matrix characteristic or property names.
     * Ordering is necessary because the weights.json output from the R script generates weights in order according
     * to the left-to-right comparison matrix it receives as input.
     * Eventually this method should be depreicated by modifying the R-script to automatically attach the names
     * to its output.
     *
     * @param comparisonMatricesDirectory
     *      Path to the directory holding the comparison matrix files to extract name order from.
     *      E.g. "src/comparison_matrices/"
     * @return
     *      Mapping of {
     *          Key: node name of weights receiver,
     *          Value: left-to-right ordered list of characteristic or property names under comparison
     *      }
     */
    static Map<String, ArrayList<String>> parseNameOrder(Path comparisonMatricesDirectory) {

        Map<String, ArrayList<String>> orderedWeightNames = new HashMap<>();
        for (final File matrixFile : Objects.requireNonNull(comparisonMatricesDirectory.toFile().listFiles())) {

            try {
                FileReader fr = new FileReader(matrixFile);
                CSVReader reader = new CSVReader(fr);
                String[] header = reader.readNext();

                String nodeName = header[0];
                ArrayList<String> weightNameOrder = new ArrayList<>(Arrays.asList(header).subList(1, header.length));

                orderedWeightNames.put(nodeName, weightNameOrder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return orderedWeightNames;
    }
}
