package pique.calibration;

import pique.analysis.ITool;
import pique.model.QualityModel;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

/**
 * Interface class used by model derivation for benchmarking
 */
public interface IBenchmarker {

    /**
     * Primary benchmarking function: Derive thresholds for a collection of {@link pique.model.ModelNode} objects
     *      given a benchmark repository
     *
     * @param benchmarkRepository
     *      The root directory containing the items to be used for benchmarking
     * @param qmDescription
     *      The quality model description file
     * @param tools
     *      The collection of static analysis tools needed to audio the benchmark repository
     * @param projectRootFlag
     *      Option flag to target the static analysis tools
     * @param analysisResults
     *      Location to place benchmarking analysis results
     * @return
     *      A dictionary of [ Key: {@link pique.model.ModelNode} name, Value: thresholds ]
     */
    Map<String, Double[]> deriveThresholds(Path benchmarkRepository, QualityModel qmDescription, Set<ITool> tools,
                                           String projectRootFlag);

    /**
     * @return An identifiable name of the benchmarker
     */
    String getName();
}
