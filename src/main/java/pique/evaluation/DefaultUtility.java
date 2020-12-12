package pique.evaluation;

// TODO (1.0): default utility function
public class DefaultUtility implements IUtilityFunction {

    /**
     * Apply linear interpolation to a 2-threshold array represeting the min and max values seen.
     */
    @Override
    public double utilityFunction(double inValue, Double[] thresholds, boolean positive) {

        // TODO: Investigate genericising this formula to n dimensions
        if (thresholds.length != 2) throw new RuntimeException("Default utility function assume a threshold array of " +
                "exactly size 2.");

        if (!positive) {
            if (inValue <= thresholds[0]) return 1.0;
            else if (inValue >= thresholds[1]) return  0.0;
            else {
                return 1 - linearInterpolationTwoPoints(inValue, thresholds);
            }
        }

        else {
            if (inValue <= thresholds[0]) return 0.0;
            else if (inValue >= thresholds[1]) return  1.0;
            else {
                return linearInterpolationTwoPoints(inValue, thresholds);
            }
        }
    }

    private double linearInterpolationTwoPoints(double inValue, Double[] thresholds) {
        return (inValue - thresholds[0]) * (1 / (thresholds[1] - thresholds[0]));
    }

}