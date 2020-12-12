package pique.evaluation;

import pique.model.ModelNode;

// TODO (1.0): Documentation
public class DefaultProductFactorEvaluator extends Evaluator {

    @Override
    public double evaluate(ModelNode inNode) {

        // TODO (1.0): Temporarly making assumption strictly one measure connected to each Product Factor (easy to
        //  support beyond this)
        return inNode.getAnyChild().getValue();
    }

}
