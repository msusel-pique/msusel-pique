package qatch.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract representation of a node belonging to the Quality, Characteristic,
 * or Property layer
 */
public abstract class ModelNode {

    // Fields
    private String description;
    private String name;
    private double value;  // the value this node evaluates to
    private Map<String, Double> weights = new HashMap<>();  // mapping of incoming node and and its weight value


    // Constructor
    public ModelNode(String name, String description) {
        this.name = name;
        this.description = description;
    }


    // Getters and setters
    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public double getValue() {
        return this.evaluate();
    }

    public Map<String, Double> getWeights() {
        return weights;
    }


    // Methods

    /**
     * Calculates the value field of this object.
     * If evaluating TQI or Characteristic nodes, this will likely be a weight sum function.
     * If evaluating Property nodes, this will likely be the linear interpolation utility function.
     *
     * @return
     *      A decimal value representing the value field of this object, a number between [0.0, 1.0].
     */
    protected abstract double evaluate();
}
