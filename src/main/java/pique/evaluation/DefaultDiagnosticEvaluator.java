package pique.evaluation;

import pique.model.Diagnostic;
import pique.model.ModelNode;

// TODO (1.0) Documentation
public class DefaultDiagnosticEvaluator extends Evaluator {

    @Override
    public double evaluate(ModelNode inNode) {
        Diagnostic node = (Diagnostic)inNode;
        return node.getChildren().values().stream()
                .mapToDouble(ModelNode::getValue)
                .sum();
    }

}
