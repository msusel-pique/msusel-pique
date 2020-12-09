package pique.evaluation;

import pique.model.Finding;
import pique.model.ModelNode;

import java.util.HashSet;
import java.util.Set;

// TODO (1.0): Documentation
public class LoCEvaluator extends Evaluator {

    public LoCEvaluator() {
        this.name = "LOCEvaluator";
    }

    @Override
    public double evalStrategy(ModelNode inNode) {
        inNode.

        Finding locFinding = modelNodeFindings.stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Could not find any findings in set of Findings input during LOCDiagnosticEvaluator evalStrategy"));
        return locFinding.getValue();
    }
}
