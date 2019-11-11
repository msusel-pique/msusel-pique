package qatch.runnable;

import qatch.analysis.ITool;
import qatch.analysis.IToolLOC;
import qatch.calibration.Benchmarker;
import qatch.calibration.WeightResult;
import qatch.calibration.Weighter;
import qatch.model.QualityModel;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

/**
 * Utility driver class responible for running the calibration module's procedure.
 * This uses a benchmark repository, quality model description, directory of comparison matrices,
 * instances of language-specific analysis tools, and the RInvoker as input to perform a 3 step process of
 *     (1) Derive thresholds
 *     (2) Elicitate weights
 *     (3) Apply these results to the quality model to generate a fully derived quality model
 */
public class QualityModelDeriver {

    public static void deriveModel(
            QualityModel qmDescription,
            IToolLOC locTool,
            Map<String, ITool> tools,
            Path benchmarkRepository,
            Path comparisonMatricesDirectory,
            Path analysisResults,
            Path rThresholdsOutput,
            Path tempWeightsDirectory,
            String projectRootFlag)
    {

        // (1) Derive thresholds
        Map<String, Double[]> measureNameThresholdMappings = Benchmarker.deriveThresholds(
                benchmarkRepository, qmDescription, locTool, tools, projectRootFlag, analysisResults, rThresholdsOutput
        );

        // (2) Elicitate weights
        Set<WeightResult> weights = Weighter.elicitateWeights(comparisonMatricesDirectory, tempWeightsDirectory);

        // (3) Apply results to nodes in quality model by matching names
        measureNameThresholdMappings.forEach((measureName, thresholds) -> {
            qmDescription.getPropertyByMeasureName(measureName).setThresholds(thresholds);
        });

        weights.forEach(weightsIn -> {
//            qmDescription.
        });

    }

}
