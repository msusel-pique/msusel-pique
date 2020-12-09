package pique.model;

import com.google.gson.annotations.Expose;
import pique.evaluation.DefaultFactorEvaluator;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the total quality index of a project.
 * It is used for the evaluation (quality assessment - estimation) of a
 * certain project or a benchmark of projects.
 *
 * @author Miltos, Rice
 */
public class Tqi extends ModelNode {

    // Fields, some inherit from super class

    @Expose
    private Map<String, Double> weights;  // mapping of quality aspects names and their weights
    private Map<String, QualityAspect> qualityAspects = new HashMap<>(); // mapping of characteristic names and their object

    // Getters and setters

    public QualityAspect getAnyQualityAspect() {
        QualityAspect anyQualityAspect = getQualityAspects().values().stream().findAny().orElse(null);
        assert anyQualityAspect != null;
        return anyQualityAspect;
    }

    public Map<String, QualityAspect> getQualityAspects() {
        return qualityAspects;
    }

    public void setQualityAspect(QualityAspect qualityAspect) {
        getQualityAspects().put(qualityAspect.getName(), qualityAspect);
    }

    public void setQualityAspects(Map<String, QualityAspect> qualityAspects) {
        this.qualityAspects = qualityAspects;
    }

    public double getWeight(String qualityAspectName) {
        return weights.get(qualityAspectName);
    }

    public Map<String, Double> getWeights() {
        return weights;
    }

    public void setWeights(Map<String, Double> weights) {
        this.weights = weights;
    }

    public void setWeight(String qualityAspectName, double value) {
        this.weights.put(qualityAspectName, value);
    }


    // Constructor

    public Tqi(String name, String description, Map<String, Double> weights) {
        super(name, description, new DefaultFactorEvaluator(), null);
        this.weights = (weights == null) ? new HashMap<>() : weights;
    }

    public Tqi(String name, String description, Map<String, Double> weights, Map<String, QualityAspect> qualityAspects) {
        super(name, description, new DefaultFactorEvaluator(), null);
        this.weights = (weights == null) ? new HashMap<>() : weights;
        this.qualityAspects = qualityAspects;
    }


    // Methods

    /**
     * Tqi clone needs to work from a bottom-up parse to allow the fully connected
     * QualityAspect -> ProductFactor layer to pass the cloned property nodes by reference.
     */
    @Override
    public ModelNode clone() {
        Map<String, QualityAspect> clonedQualityAspects = new HashMap<>();
        Map<String, ProductFactor> clonedProductFactors = new HashMap<>();

        getAnyQualityAspect().getProductFactors().values().forEach(productFactor -> {
            ProductFactor clonedProductFactor = (ProductFactor) productFactor.clone();
            clonedProductFactors.put(clonedProductFactor.getName(), clonedProductFactor);
        });

        getQualityAspects().values().forEach(characteristic -> {
            QualityAspect clonedQualityAspect = (QualityAspect) characteristic.clone(clonedProductFactors);
            clonedQualityAspects.put(clonedQualityAspect.getName(), clonedQualityAspect);
        });

        return new Tqi(getName(), getDescription(), getWeights(), clonedQualityAspects);
    }


    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Tqi)) {
            return false;
        }
        Tqi otherTqi = (Tqi) other;

        return getName().equals(otherTqi.getName())
                && getQualityAspects().size() == otherTqi.getQualityAspects().size();
    }

}
