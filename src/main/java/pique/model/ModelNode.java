package pique.model;

import com.google.gson.annotations.Expose;
import pique.evaluation.DefaultNormalizer;
import pique.evaluation.LoCNormalizer;
import pique.evaluation.IEvaluator;
import pique.evaluation.INormalizer;

/**
 * Abstract representation of a node belonging to the Quality Model
 */
public abstract class ModelNode {

    // Fields
    @Expose
    protected double value;  // the value this node evaluates to

    @Expose
    protected String description;
    protected String name;

    protected IEvaluator evaluator;
    protected INormalizer normalizer;

    // Constructor

    public ModelNode(String name, String description, IEvaluator evaluator) {
        this.name = name;
        this.description = description;
        this.evaluator = evaluator;
        this.normalizer = new DefaultNormalizer();
    }

    public ModelNode(String name, String description, IEvaluator evaluator, INormalizer normalizer) {
        this.name = name;
        this.description = description;
        this.evaluator = evaluator;
        this.normalizer = normalizer;
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

    public double getValue() {
        evaluate();
        return this.value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public IEvaluator getEvaluator() {
        return evaluator;
    }

    public void setEvaluator(IEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    public INormalizer getNormalizer() {
        return normalizer;
    }

    public void setNormalizer(INormalizer normalizer) {
        this.normalizer = normalizer;
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
     * TODO (1.0): Documentation
     */
    protected void evaluate()
    {
        this.value = evaluator.evalStrategy();
    }
}
