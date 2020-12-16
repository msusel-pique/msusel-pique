package pique.evaluation;

// TODO (1.0): default utility function
public class DefaultUtility implements IUtilityFunction {

    /**
     * Apply linear interpolation to a 2-threshold array represeting the min and max values seen.
     * TODO: Investigate genericising this formula to n dimensions
     */
    @Override
    public double utilityFunction(double inValue, Double[] thresholds, boolean positive) {

        // TODO (1.0): Major fix needed as revealed here. Need to think about how to better go about cloning an
        //  instancing quality models and "Project" objects

        // If no thresholds yet, currently dealing with a non-derived model. Just return 0.
        if (thresholds == null) return 0.0;

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