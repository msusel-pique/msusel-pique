package pique.evaluation;

public abstract class Normalizer implements INormalizer {

    protected String name = this.getClass().getSimpleName();

    protected double normalizerValue = 1.0;

    @Override
    public String getName() { return this.getClass().getCanonicalName();
    }

    @Override
    public void setNormalizerValue(double value) {
        this.normalizerValue = value;
    }
}
