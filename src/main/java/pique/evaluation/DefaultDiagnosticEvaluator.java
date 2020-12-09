package pique.evaluation;

import pique.model.Finding;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class DefaultDiagnosticEvaluator implements IEvaluator {
    @Override
    public double evalStrategy() {
        throw new NotImplementedException();
    }

    @Override
    public String getName() {
        return "pique.evaluation.DefaultDiagnosticEvaluator";
    }
}
