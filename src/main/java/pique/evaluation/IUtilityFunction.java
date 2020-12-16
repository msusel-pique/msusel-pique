package pique.evaluation;

public interface IUtilityFunction {

    /**
     * Apply a utility function given an input value and thresholds
     *
     * @param inValue
     *      The value before applying the utility functions
     * @param thresholds
     *      The thresholds to use in the utility function. Likely comes from benchmarking
     * @param positive
     *      Whether or not the input value has a positive or negative effect on quality
     * @return
     *      The value of inValue after applying the utility function
     */
    double utilityFunction(double inValue, Double[] thresholds, boolean positive);

}
