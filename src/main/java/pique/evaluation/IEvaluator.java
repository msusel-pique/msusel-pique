package pique.evaluation;

import pique.model.Diagnostic;
import pique.model.Finding;

import java.util.Set;
import java.util.function.Function;

public interface IEvaluator {

    Function<Set<Finding>, Double> evalStrategy();

    String getName();

}
