package qatch.model;

/**
 * Abstract representation of a node belonging to the Quality, Characteristic,
 * or Property layer
 */
public abstract class ModelNode {

    // Fields
    private String description;
    private String name;
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
    public String getName() {
        return name;
    }
    public double getValue() { return this.value; }


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
