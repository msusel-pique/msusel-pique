package qatch.calibration;

import qatch.model.QualityModel;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.nio.file.Path;

/**
 * This class is responsible for deriving the weights (TQI-Characteristics layer and Characteristics-Properties layer)
 * described by hand-entered comparison matrices as part of the analytical hierarchy process.
 *
 * It should ensure the hand-entered matrices match TQI, characteristic, and properties nodes
 * described in the quality model and the data format in the matrices is valid.
 *
 * The class should provide functionality to return a mapping of weights over the two layers given
 * a quality model description and comparison matrices as input by running R scripts.
 */
public class Weighter {

    // Fields
    private Path comparisonMatricesDirectory;  // location of root folder containing comparison matrices (likely .csv)
    private QualityModel qmDescription;  // object representation of a not-yet-derived quality model


    // Constructor
    public Weighter(Path comparisonMatricesDirectory, QualityModel qmDescription) {
        this.comparisonMatricesDirectory = comparisonMatricesDirectory;
        this.qmDescription = qmDescription;
    }


    // Getters and setters
    public Path getComparisonMatricesDirectory() { return comparisonMatricesDirectory; }

    public QualityModel getQmDescription() { return qmDescription; }


    // Methods
    public void run() {
        throw new NotImplementedException();
    }
}
