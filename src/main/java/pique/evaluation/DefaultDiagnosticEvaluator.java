package pique.evaluation;

import pique.model.Finding;

import java.util.Set;
import java.util.function.Function;

public class DefaultDiagnosticEvaluator implements IEvaluator {
    @Override
    public Function<Set<Finding>, Double> evalStrategy() {
        return findings -> findings.stream().mapToDouble(Finding::getSeverity).sum();
    }

    @Override
    public String getName() {
        return "pique.evaluation.DefaultDiagnosticEvaluator";
    }
}
