package pique.evaluation;

import pique.model.Finding;

import java.util.Set;
import java.util.function.Function;

public class DefaultMeasureEvaluator implements IEvaluator {

    @Override
    public Function<Set<Finding>, Double> evalStrategy() {
        return findings -> 1.0;
    }

    @Override
    public String getName() {
        return "pique.evaluation.DefaultMeasureEvaluator";
    }
}
