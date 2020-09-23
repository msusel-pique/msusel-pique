package pique.evaluation;

import pique.model.Finding;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Set;
import java.util.function.Function;

//TODO: write class
public class DefaultFactorEvaluator implements IEvaluator {
    @Override
    public Function<Set<Finding>, Double> evalStrategy() {
        throw new NotImplementedException();
    }

    @Override
    public String getName() {
        throw new NotImplementedException();
    }
}
