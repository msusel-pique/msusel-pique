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
    protected String name;
    @Expose
    protected double value;  // the value this node evaluates to
    @Expose
    protected String description;
    // TODO: eventually consider new tree object that combines properties and their weight instead of relying on
    //  String name matching (not enough time for me to solve currently)
    @Expose
    protected Map<String, ModelNode> children = new HashMap<>();
    @Expose
    protected Map<String, Double> weights = new HashMap<>();
    @Expose
    protected Double[] thresholds;
    @Expose
    protected String eval_strategy;
    @Expose
    protected String normalizer;
    @Expose
    protected String utility_function;

    protected IEvaluator evaluatorObject;
    protected INormalizer normalizerObject;
    protected IUtilityFunction utilityFunctionObject;

    protected boolean visited = false;      // Use for BFS traversal

    // Constructor

    public ModelNode(String name, String description, IEvaluator evaluatorObject, INormalizer normalizerObject) {
        this.name = name;
        this.description = description;
        this.evaluatorObject = evaluatorObject;
        this.normalizerObject = normalizerObject;
        this.utilityFunctionObject = new DefaultUtility();

        this.eval_strategy    = evaluatorObject.getClass().getCanonicalName();
        this.normalizer       = normalizerObject.getClass().getCanonicalName();
        this.utility_function = utilityFunctionObject.getClass().getCanonicalName();
    }

    public ModelNode(String name, String description, IEvaluator evaluatorObject, INormalizer normalizerObject,
                     IUtilityFunction utilityFunctionObject, Map<String, Double> weights, Double[] thresholds) {
        this.name = name;
        this.description = description;
        this.evaluatorObject = evaluatorObject;
        this.normalizerObject = normalizerObject;
        this.utilityFunctionObject = utilityFunctionObject;
        if (weights != null) this.weights = weights;
        if (thresholds != null) this.thresholds = thresholds;

        this.eval_strategy    = evaluatorObject.getClass().getCanonicalName();
        this.normalizer       = normalizerObject.getClass().getCanonicalName();
        this.utility_function = utilityFunctionObject.getClass().getCanonicalName();
    }

    /**
     * Constructor for cloning.
     */
    public ModelNode(double value, String name, String description, IEvaluator evaluatorObject, INormalizer normalizerObject,
                     IUtilityFunction utilityFunctionObject, Map<String, Double> weights, Double[] thresholds, Map<String,
            ModelNode> children) {
        this.value = value;
        this.name = name;
        this.description = description;
        this.evaluatorObject = evaluatorObject;
        this.normalizerObject = normalizerObject;
        this.utilityFunctionObject = utilityFunctionObject;
        this.weights = weights;
        this.thresholds = thresholds;
        this.children = children;

        this.eval_strategy    = evaluatorObject.getClass().getCanonicalName();
        this.normalizer       = normalizerObject.getClass().getCanonicalName();
        this.utility_function = utilityFunctionObject.getClass().getCanonicalName();
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

    public IEvaluator getEvaluatorObject() {
        return evaluatorObject;
    }

    public void setEvaluatorObject(IEvaluator evaluatorObject) {
        this.evaluatorObject = evaluatorObject;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public INormalizer getNormalizerObject() {
        return normalizerObject;
    }

    public void setNormalizerObject(INormalizer normalizerObject) {
        this.normalizerObject = normalizerObject;
    }

    public void setNormalizerValue(double value) {
        getNormalizerObject().setNormalizerValue(value);
    }

    public int getNumChildren() { return getChildren().size(); }

    public IUtilityFunction getUtilityFunctionObject() {
        return utilityFunctionObject;
    }

    public void setUtilityFunctionObject(IUtilityFunction utilityFunctionObject) {
        this.utilityFunctionObject = utilityFunctionObject;
    }

    public double getValue() {
        evaluate();
        return this.value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
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
        setValue(getEvaluatorObject().evaluate(this));
    }

    //endregion
}
