package pique.evaluation;

import pique.model.Finding;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class DefaultDiagnosticEvaluator extends Evaluator {

    public DefaultDiagnosticEvaluator() {
        this.name = "DefaultDiagnosticEvaluator";
    }

    @Override
    public double evalStrategy(double inValue, Object... args) {
        throw new NotImplementedException();
    }

}
