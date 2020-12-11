package pique.evaluation;

import pique.model.Finding;
import pique.model.ModelNode;

public class DefaultFindingEvaluator extends Evaluator {

    @Override
    public double evaluate(ModelNode inNode) {
        Finding node = (Finding)inNode;
        return node.getSeverity();
    }

}
