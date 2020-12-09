package pique.evaluation;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class DefaultMeasureEvaluator implements IEvaluator {

    // TODO (1.0): fix
    @Override
    public double evalStrategy() {
        throw new NotImplementedException();
    }

    @Override
    public String getName() {
        return "pique.evaluation.DefaultMeasureEvaluator";
    }
}
