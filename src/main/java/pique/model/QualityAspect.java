package pique.model;

import pique.evaluation.DefaultFactorEvaluator;
import pique.evaluation.IEvaluator;
import pique.evaluation.INormalizer;
import pique.evaluation.IUtilityFunction;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a quality characteristic of the
 * Quality Model that is used in order to evaluate a
 * project or a set of projects.
 */
public class QualityAspect extends ModelNode {

    //region Constructors

    public QualityAspect(String name, String description) {

        super(name, description, new DefaultFactorEvaluator(), null);
    }

    public QualityAspect(String name, String description, IEvaluator evaluator, INormalizer normalizer,
                         IUtilityFunction utilityFunction, Map<String, Double> weights, Double[] thresholds) {
        super(name, description, evaluator, normalizer, utilityFunction, weights, thresholds);
    }

    public QualityAspect(String name, String description, Map<String, Double> weights) {
        super(name, description, new DefaultFactorEvaluator(), null);
        this.weights = weights;
    }

    public QualityAspect(String name, String description, Map<String, Double> weights,
                         Map<String, ModelNode> productFactors) {
        super(name, description, new DefaultFactorEvaluator(), null);
        this.weights = weights;
        this.children = productFactors;
    }

    // Used for cloning
    public QualityAspect(double value, String name, String description, IEvaluator evaluator, INormalizer normalizer,
               IUtilityFunction utilityFunction, Map<String, Double> weights, Double[] thresholds, Map<String,
            ModelNode> children) {
        super(value, name, description, evaluator, normalizer, utilityFunction, weights, thresholds, children);
    }

    //endregion


    //region Methods

    @Override
    public ModelNode clone() {

        Map<String, ModelNode> clonedChildren = new HashMap<>();
        getChildren().forEach((k, v) -> clonedChildren.put(k, v.clone()));

        return new QualityAspect(getValue(), getName(), getDescription(), getEvaluatorObject(), getNormalizerObject(),
                getUtilityFunctionObject(), getWeights(), getThresholds(), clonedChildren);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof QualityAspect)) {
            return false;
        }
        QualityAspect otherQualityAspect = (QualityAspect) other;

        return getName().equals(otherQualityAspect.getName())
                && getChildren().size() == otherQualityAspect.getChildren().size();
    }

    //endregion
}
