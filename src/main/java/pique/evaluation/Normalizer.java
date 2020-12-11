package pique.evaluation;

public abstract class Normalizer implements INormalizer {

    protected String name = this.getClass().getSimpleName();

    @Override
    public String getName() { return this.getClass().getCanonicalName();
    }

}
