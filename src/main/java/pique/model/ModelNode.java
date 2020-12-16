package pique.model;

import com.google.gson.annotations.Expose;
import pique.evaluation.DefaultUtility;
import pique.evaluation.IEvaluator;
import pique.evaluation.INormalizer;
import pique.evaluation.IUtilityFunction;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract representation of a node belonging to the Quality Model
 */
public abstract class ModelNode {

    //region Fields
    @Expose
    protected double value;  // the value this node evaluates to
    @Expose
    protected String name;
    @Expose
    protected String description;
    @Expose
    protected IEvaluator evaluator;
    @Expose
    protected INormalizer normalizer;
    @Expose
    protected IUtilityFunction utilityFunction;
    // TODO: eventually consider new tree object that combines properties and their weight instead of relying on
    //  String name matching (not enough time for me to solve currently)
    @Expose
    protected Map<String, ModelNode> children = new HashMap<>();
    @Expose
    protected Map<String, Double> weights = new HashMap<>();
    @Expose
    protected Double[] thresholds;

    // Constructor

    public ModelNode(String name, String description, IEvaluator evaluator, INormalizer normalizer) {
        this.name = name;
        this.description = description;
        this.evaluator = evaluator;
        this.normalizer = normalizer;
        this.utilityFunction = new DefaultUtility();
    }

    public ModelNode(String name, String description, IEvaluator evaluator, INormalizer normalizer,
                     IUtilityFunction utilityFunction, Map<String, Double> weights, Double[] thresholds) {
        this.name = name;
        this.description = description;
        this.evaluator = evaluator;
        this.normalizer = normalizer;
        this.utilityFunction = utilityFunction;
        if (weights != null) this.weights = weights;
        if (thresholds != null) this.thresholds = thresholds;
    }

    /**
     * Constructor for cloning.
     */
    public ModelNode(double value, String name, String description, IEvaluator evaluator, INormalizer normalizer,
                     IUtilityFunction utilityFunction, Map<String, Double> weights, Double[] thresholds, Map<String,
            ModelNode> children) {
        this.value = value;
        this.name = name;
        this.description = description;
        this.evaluator = evaluator;
        this.normalizer = normalizer;
        this.utilityFunction = utilityFunction;
        this.weights = weights;
        this.thresholds = thresholds;
        this.children = children;
    }

    //endregion


    //region Getters and setters

    public ModelNode getAnyChild() {
        ModelNode anyModelNode = getChildren().values().stream().findAny().orElse(null);
        assert anyModelNode != null;
        return anyModelNode;
    }

    public ModelNode getChild(String name) {
        return getChildren().get(name);
    }

    public void setChild(ModelNode child) {
        getChildren().put(child.getName(), child);
    }

    public Map<String, ModelNode> getChildren() { return children; }

    public void setChildren(Map<String, ModelNode> children) {
        this.children = children;
    }

    public void setChildren(Collection<ModelNode> children) {
        children.forEach(element -> getChildren().putIfAbsent(element.getName(), element));
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String desription) {
        this.description = desription;
    }

    public IEvaluator getEvaluator() {
        return evaluator;
    }

    public void setEvaluator(IEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public INormalizer getNormalizer() {
        return normalizer;
    }

    public void setNormalizer(INormalizer normalizer) {
        this.normalizer = normalizer;
    }

    public void setNormalizerValue(double value) {
        getNormalizer().setNormalizerValue(value);
    }

    public int getNumChildren() { return getChildren().size(); }

    public IUtilityFunction getUtilityFunction() {
        return utilityFunction;
    }

    public void setUtilityFunction(IUtilityFunction utilityFunction) {
        this.utilityFunction = utilityFunction;
    }

    public double getValue() {
        evaluate();
        return this.value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Double[] getThresholds() {
        return thresholds;
    }

    public void setThresholds(Double[] thresholds) {
        this.thresholds = thresholds;
    }

    public double getWeight(String modelNodeName) {
        try {
            return weights.get(modelNodeName);
        }
        // TODO (1.0): Some redesign needed to better handle quality model description where there are not yet weights,
        //  values, etc...
        catch (NullPointerException e) { return 0.0; }
    }

    public Map<String, Double> getWeights() {
        return weights;
    }

    public void setWeight(String modelNodeName, double value) {
        this.weights.put(modelNodeName, value);
    }

    public void setWeights(Map<String, Double> weights) {
        this.weights = weights;
    }

    //endregion


    //region Methods

    /**
     * A loose way of implementing the Prototype design pattern.
     * Each quality model node must describe how to clone itself.
     *
     * @return A deep clone of the current node object.
     */
    public abstract ModelNode clone();

    /**
     * TODO (1.0): Documentation
     */
    protected void evaluate() {
        setValue(getEvaluator().evaluate(this));
    }

    //endregion
}
