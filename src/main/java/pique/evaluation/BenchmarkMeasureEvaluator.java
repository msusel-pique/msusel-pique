package pique.evaluation;

import pique.model.ModelNode;

/**
 * Evaluator used during benchmark repository analysis
 */
public class BenchmarkMeasureEvaluator extends Evaluator {

    /**
     * Return the sum of this node's children normalized
     */
    @Override
    public double evaluate(ModelNode inNode) {
        double rawSum = inNode.getChildren().values().stream()
                .mapToDouble(ModelNode::getValue)
                .sum();

        return inNode.getNormalizerObject().normalize(rawSum);
    }
}
