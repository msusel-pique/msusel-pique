package qatch.runnable;

import qatch.analysis.ITool;
import qatch.analysis.IToolLOC;
import qatch.calibration.Benchmarker;
import qatch.calibration.WeightResult;
import qatch.calibration.Weighter;
import qatch.model.Characteristic;
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

    public static QualityModel deriveModel(
            QualityModel qmDescription,
            IToolLOC locTool,
            Map<String, ITool> tools,
            Path benchmarkRepository,
            Path comparisonMatricesDirectory,
            Path analysisResults,
            Path rThresholdsOutput,
            Path tempWeightsDirectory,
            String projectRootFlag) {

        // (1) Derive thresholds
        Map<String, Double[]> measureNameThresholdMappings = Benchmarker.deriveThresholds(
                benchmarkRepository, qmDescription, locTool, tools, projectRootFlag, analysisResults, rThresholdsOutput
        );

        // (2) Elicitate weights
        Set<WeightResult> weights = Weighter.elicitateWeights(comparisonMatricesDirectory, tempWeightsDirectory);

        // (3) Apply results to nodes in quality model by matching names
        // Thresholds (Property nodes)
        measureNameThresholdMappings.forEach((measureName, thresholds) -> {
            qmDescription.getPropertyByMeasureName(measureName).setThresholds(thresholds);
        });

        // Weights (TQI and Characteristic nodes)
        for (WeightResult weightsIn : weights) {
            // Check root node case
            if (qmDescription.getTqi().getName().equals(weightsIn.name)) { qmDescription.getTqi().setWeights(weightsIn.weights); }
            // Otherwise search though Characteristic nodes
            else {
                for (Characteristic characteristic : qmDescription.getCharacteristics().values()) {
                    if (characteristic.getName().equals(weightsIn.name)) {
                        characteristic.setWeights(weightsIn.weights);
                        break;
                    }
                }
            }
        }

        // Assert postcondition that weights and thresholds now exist for all appropriate model nodes
        // TQI weights check
        for (String characteristicWeightName : qmDescription.getCharacteristics().keySet()) {
            if (!qmDescription.getTqi().getWeights().containsKey(characteristicWeightName)) {
                throw new RuntimeException("After running weight elicitation, no weight mapping found for TQI node and " +
                        "Characteristic node, " + characteristicWeightName);
            }
        }

        // Characteristics weights check
        qmDescription.getCharacteristics().values().forEach(characteristic -> {
            for (String propertyWeightName : qmDescription.getProperties().keySet()) {
                if (!characteristic.getWeights().containsKey(propertyWeightName)) {
                    throw new RuntimeException("After running weight elicitation, no weight mapping found for Characteristic" +
                            " node, " + characteristic.getName() + ", and " +
                            "Property node, " + propertyWeightName);
                }
            }
        });

        // Properties thresholds check
        qmDescription.getProperties().values().forEach(property -> {
            if (property.getThresholds().length != 3) {
                throw new RuntimeException("After running threshold derivation, the threshold size of " +
                    "property, " + property.getName() + ", does not equal 3");
            }
        });

        return qmDescription;

    }
}
