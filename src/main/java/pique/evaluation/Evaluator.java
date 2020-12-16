package pique.evaluation;

public abstract class Evaluator implements IEvaluator {

    protected String name = this.getClass().getSimpleName();

    @Override
    public String getName() { return this.getClass().getCanonicalName();
    }
}
