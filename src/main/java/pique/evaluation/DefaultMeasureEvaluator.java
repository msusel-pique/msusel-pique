package pique.evaluation;

import pique.model.Measure;
import pique.model.ModelNode;

// TODO (1.0) Documentation
public class DefaultMeasureEvaluator extends Evaluator {

    /**
     * By default, a measure should evaluate as the sum of its diagnostics normalized by its normalizer.
     * Note: for measure metrics, this will likely be different functionality and should be overridden as so.
     */
    @Override
    public double evaluate(ModelNode inNode) {
        Measure node = (Measure)inNode;

        // Sum values of child diagnostics
        double value = node.getChildren().values().stream()
                .mapToDouble(ModelNode::getValue)
                .sum();

        // Normalize
        value = node.getNormalizerObject().normalize(value);

        // Apply utility function
        return node.getUtilityFunctionObject().utilityFunction(value, node.getThresholds(), node.isPositive());
    }
}
