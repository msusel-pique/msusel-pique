package pique.evaluation;

import pique.model.Finding;

import java.util.Set;
import java.util.function.Function;

public class LOCDiagnosticEvaluator implements IEvaluator {

    @Override
    public Function<Set<Finding>, Double> evalStrategy() {
        return findings -> {
            Finding locFinding = findings.stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Could not find any findings in set of Findings input during LOCDiagnosticEvaluator evalStrategy"));
            return locFinding.getValue();
        };
    }

    @Override
    public String getName() {
        return "pique.evaluation.LOCDiagnosticEvaluator";
    }
}
