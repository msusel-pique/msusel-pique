package pique.evaluation;

import pique.model.Measure;
import pique.model.ModelNode;

import java.util.Collections;

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
        value = node.getNormalizer().normalize(value);

        // Apply utility function
        return node.getUtilityFunction().utilityFunction(value, node.getThresholds(), node.isPositive());
    }
}
