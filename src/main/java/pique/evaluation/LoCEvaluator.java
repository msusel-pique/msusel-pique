package pique.evaluation;

import pique.model.Finding;
import pique.model.ModelNode;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashSet;
import java.util.Set;

// TODO (1.0): Documentation
public class LoCEvaluator extends Evaluator {

    // TODO (1.0): Documentation
    @Override
    public double evaluate(ModelNode inNode) {

        // TODO (1.0): Add error handling
        throw new NotImplementedException();

//        Finding locFinding = modelNodeFindings.stream()
//                .findFirst()
//                .orElseThrow(() -> new RuntimeException("Could not find any findings in set of Findings input during LOCDiagnosticEvaluator evalStrategy"));
//        return locFinding.getValue();
    }
}
