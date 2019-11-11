package qatch.model;

import com.google.gson.annotations.Expose;

/**
 * Abstract representation of a node belonging to the Quality, Characteristic,
 * or Property layer
 */
public abstract class ModelNode {

    // Fields
    private String description;
    @Expose
    private String name;
    @Expose
    private double value;  // the value this node evaluates to


    // Constructor
    public ModelNode(String name, String description) {
        this.name = name;
        this.description = description;
    }


    // Getters and setters
    public String getDescription() {
        return description;
    }
    public void setDescription(String desription) {
        this.description = desription;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public double getValue() { return this.value; }
    public void setValue(double value) {
        this.value = value;
    }


    // Methods
    /**
     * Calculates and update the value field of this object.
     * If evaluating TQI or Characteristic nodes, this will likely be a weight sum function.
     * If evaluating Property nodes, this will likely be the linear interpolation utility function.
     *
     * Post:
     *      The value field is updated.
     */
    public abstract void evaluate();
}
