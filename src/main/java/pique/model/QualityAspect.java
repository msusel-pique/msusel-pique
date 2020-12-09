package pique.model;

import com.google.gson.annotations.Expose;
import pique.evaluation.DefaultFactorEvaluator;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a quality characteristic of the
 * Quality Model that is used in order to evaluate a
 * project or a set of projects.
 *
 * @author Miltos, Rice
 */
public class QualityAspect extends ModelNode {

    // Instance variables

    // TODO: eventually consider new tree object that combines properties and their weight instead of relying on String name matching (not enough time for me to refactor currently)
    private Map<String, ProductFactor> productFactors = new HashMap<>();  // mapping of productFactors names and their property objects
    @Expose
    private Map<String, Double> weights = new HashMap<>();  // mapping of productFactors names and their weights


    // Constructors

    public QualityAspect(String name, String description) {

        super(name, description, new DefaultFactorEvaluator());
    }

    public QualityAspect(String name, String description, Map<String, Double> weights) {
        super(name, description, new DefaultFactorEvaluator());
        this.weights = weights;
    }

    public QualityAspect(String name, String description, Map<String, Double> weights, Map<String, ProductFactor> productFactors) {
        super(name, description, new DefaultFactorEvaluator());
        this.weights = weights;
        this.productFactors = productFactors;
    }


    // Getters and setters

    public Map<String, ProductFactor> getProductFactors() {
        return productFactors;
    }

    public void setProductFactor(ProductFactor productFactor) {
        getProductFactors().put(productFactor.getName(), productFactor);
    }

    public void setProductFactors(Map<String, ProductFactor> productFactors) {
        this.productFactors = productFactors;
    }

    public double getWeight(String productFactorName) {
        return this.weights.get(productFactorName);
    }

    public Map<String, Double> getWeights() {
        return weights;
    }

    public void setWeight(String productFactorName, double value) {
        this.weights.put(productFactorName, value);
    }

    public void setWeights(Map<String, Double> weights) {
        this.weights = weights;
    }


    // Methods

    @Override
    public ModelNode clone() {
        System.out.println("--- WARNING ---\nCloning QualityAspect node without cloned property map.\n" +
                "This will result in an empty properties field for this QualityAspect object.");
        return new QualityAspect(getName(), getDescription(), getWeights(), new HashMap<>());
    }


    public ModelNode clone(Map<String, ProductFactor> clonedProductFactors) {
        return new QualityAspect(getName(), getDescription(), getWeights(), clonedProductFactors);
    }


    @Override
    public boolean equals(Object other) {
        if (!(other instanceof QualityAspect)) {
            return false;
        }
        QualityAspect otherQualityAspect = (QualityAspect) other;

        return getName().equals(otherQualityAspect.getName())
                && getProductFactors().size() == otherQualityAspect.getProductFactors().size();
    }
}
