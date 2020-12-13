package pique.runnable;

import pique.analysis.ITool;
import pique.calibration.IBenchmarker;
import pique.calibration.IWeighter;
import pique.calibration.WeightResult;
import pique.model.Measure;
import pique.model.ModelNode;
import pique.model.QualityAspect;
import pique.model.QualityModel;

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

    public static QualityModel deriveModel(QualityModel qmDesign, Set<ITool> tools,
                                           ITool locTool, Path benchmarkRepository,
                                           String projectRootFlag) {

        // (1) Derive thresholds
        IBenchmarker benchmarker = qmDesign.getBenchmarker();
        Map<String, Double[]> measureNameThresholdMappings = benchmarker.deriveThresholds(
                benchmarkRepository, qmDesign, tools, locTool, projectRootFlag);

        // (2) Elicitate weights
        IWeighter weighter = qmDesign.getWeighter();
        Set<WeightResult> weights = weighter.elicitateWeights();
        // TODO: assert WeightResult names match expected TQI, QualityAspect, and ProductFactor names from quality model description

        // (3) Apply results to nodes in quality model by matching names
        // Thresholds (ProductFactor nodes)
        measureNameThresholdMappings.forEach((measureName, thresholds) -> {
            Measure measure = (Measure)qmDesign.getMeasureByName(measureName);
            measure.setThresholds(thresholds);
        });

        // Weights (TQI and QualityAspect nodes)
        for (WeightResult weightsIn : weights) {
            // Check root node case
            if (qmDesign.getTqi().getName().equals(weightsIn.name)) { qmDesign.getTqi().setWeights(weightsIn.weights); }

            // Otherwise search though QualityAspect nodes
            else {
                for (ModelNode qualityAspect : qmDesign.getQualityAspects().values()) {
                    if (qualityAspect.getName().equals(weightsIn.name)) {
                        qualityAspect.setWeights(weightsIn.weights);
                        break;
                    }
                }
            }
        }

        // Assert postcondition that weights and thresholds now exist for all appropriate model nodes
        // TQI weights check
        for (String characteristicWeightName : qmDesign.getQualityAspects().keySet()) {
            if (!qmDesign.getTqi().getWeights().containsKey(characteristicWeightName)) {
                throw new RuntimeException("After running weight elicitation, no weight mapping found for TQI node and " +
                        "QualityAspect node, " + characteristicWeightName);
            }
        }

        // Characteristics weights check
        qmDesign.getQualityAspects().values().forEach(characteristic -> {
            for (String propertyWeightName : qmDesign.getProductFactors().keySet()) {
                if (!characteristic.getWeights().containsKey(propertyWeightName)) {
                    throw new RuntimeException("After running weight elicitation, no weight mapping found for QualityAspect" +
                            " node, " + characteristic.getName() + ", and " +
                            "ProductFactor node, " + propertyWeightName);
                }
            }
        });

        // Properties thresholds check
        // TODO (1.0) update once low-level tests passing
//        qmDesign.getProductFactors().values().forEach(property -> {
//            if (!(property.getMeasure().getThresholds().length >= 1)) {
//                throw new RuntimeException("After running threshold derivation, the threshold size of " +
//                    "property, " + property.getName() + ", does not equal at least 1");
//            }
//        });

        return qmDesign;

    }
}
