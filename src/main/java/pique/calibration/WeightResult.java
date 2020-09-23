package pique.calibration;

import java.util.HashMap;
import java.util.Map;

/**
 * Struct type class for representing the results of R weight elicitation.
 */
public class WeightResult {

    // Fields
    /*
     * Note: fields are public due to Sun Java coding guidelines stance on struct classes.
     * See https://www.oracle.com/technetwork/java/javase/documentation/codeconventions-137265.html#177
     * for more information.
     */
    public String name;  // the name of the ModelNode object these weights apply to
    public Map<String, Double> weights;  // weight mapping of {Key: incoming weight ModelNode name, Value: weight value}


    // Constructor
    public WeightResult(String name) {
        this.name = name;
        this.weights = new HashMap<>();
    }


    /// Getters and setters ///

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Double> getWeights() {
        return weights;
    }
    public void setWeight(String weightName, Double value) {
        getWeights().put(weightName, value);
    }
    public void setWeights(Map<String, Double> weights) {
        this.weights = weights;
    }
}
