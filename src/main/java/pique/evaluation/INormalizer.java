package pique.evaluation;

// TODO (1.0): Documentation
public interface INormalizer {

    // TODO: Think about supporting more than one value for normlaizer value
    double normalize(double inValue);

    String getName();

    /**
     * Set after static analysis tool run.
     */
    void setNormalizerValue(double value);


}
