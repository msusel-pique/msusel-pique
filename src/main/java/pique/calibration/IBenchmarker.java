package pique.calibration;

import pique.analysis.ITool;
import pique.model.QualityModel;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

public interface IBenchmarker {

    Map<String, Double[]> deriveThresholds(Path benchmarkRepository, QualityModel qmDescription, Set<ITool> tools, String projectRootFlag, Path analysisResults, Path rThresholdsOutput);

    double utilityFunction(double inValue, Double[] thresholds, boolean positive);

    String getName();
}
