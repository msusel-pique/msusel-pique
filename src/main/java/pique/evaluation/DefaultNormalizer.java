package pique.evaluation;

// TODO (1.0): Documentation
public class DefaultNormalizer extends Normalizer {

    @Override
    public double normalize(double inValue) {
        if (this.normalizerValue == 0.0) {
            throw new RuntimeException("Normalizer value was not set to a non-zero value before attempting " +
                    "normalization");
        }
        return inValue / this.normalizerValue;
    }
}
