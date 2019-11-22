package qatch.model;

import com.google.gson.annotations.Expose;

/**
 * Abstract representation of a node belonging to the Quality Model
 */
public abstract class ModelNode {

    // Fields

    @Expose
    private String name;
    @Expose
    private String description;
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
    public double getValue(Double... args) {
        evaluate(args);
        return this.value;
    }
    public void setValue(double value) {
        this.value = value;
    }


    // Methods

    /**
     * A loose way of implementing the Prototype design pattern.
     * Each quality model node must describe how to clone itself.
     *
     * @return
     *      A deep clone of the current node object.
     */
    public abstract ModelNode clone();

    /**
     * Calculates and update the value field of this object.
     * If evaluating TQI or Characteristic nodes, this will likely be a weight sum function.
     * If evaluating Property nodes, this will likely be the linear interpolation utility function.
     *
     * Post:
     *      The value field is updated.
     */
    protected abstract void evaluate(Double... args);


}
