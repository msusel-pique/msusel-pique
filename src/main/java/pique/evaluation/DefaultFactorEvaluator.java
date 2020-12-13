package pique.evaluation;

import pique.model.ModelNode;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

//TODO (1.0): Documentation
public class DefaultFactorEvaluator extends Evaluator {

    @Override
    public double evaluate(ModelNode inNode) {

        // TODO (1.0): Some redesign needed to better handle quality model description where there are not yet weights,
        //  values, etc...

        double outValue = 0.0;

        // Apply weighted sums
        for (ModelNode child : inNode.getChildren().values()) {
            outValue += child.getValue() * inNode.getWeight(child.getName());
        }

        return outValue;
    }
}
