package pique.utility;

import pique.evaluation.Evaluator;
import pique.model.ModelNode;

public class DemoProductFactorEvaluator extends Evaluator {

    @Override
    public double evaluate(ModelNode inNode) {

        double weightedSum = 0.0;
        for (ModelNode child : inNode.getChildren().values()) {
            weightedSum += child.getValue();
        }
        weightedSum = weightedSum / (double)inNode.getChildren().size();

        return  weightedSum / 2.0;
    }
}
