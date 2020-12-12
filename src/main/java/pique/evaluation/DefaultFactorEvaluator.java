package pique.evaluation;

import pique.model.ModelNode;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

//TODO (1.0): Documentation
public class DefaultFactorEvaluator extends Evaluator {

    @Override
    public double evaluate(ModelNode inNode) {

        double outValue = 0.0;

        // Apply weighted sums
        for (ModelNode child : inNode.getChildren().values()) {
            outValue += child.getValue() * inNode.getWeight(child.getName());
        }

        return outValue;
    }
}
