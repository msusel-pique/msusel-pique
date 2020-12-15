package pique.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import pique.calibration.IBenchmarker;
import pique.calibration.IWeighter;
import pique.calibration.NaiveBenchmarker;
import pique.calibration.NaiveWeighter;
import pique.evaluation.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Class responsible for parsing quality model file data and transforming it into a {@link ModelNode} object tree.
 * <p>
 * Note, this class is entirely dependent on the assumption that the quality model file is written in a specific,
 * intenional syntax.
 * Pre and post checks, and more robost design is left as future work.
 * <p>
 * This class is capable of importing a quality model description (no edge weights or threshold arrays) as well as a
 * derived  quality model (includes edge weights and threshold arrays). The user does not need to distingust between
 * the two.
 */
public class QualityModelImport {

    // Private Fields
    QualityModel qualityModel = new QualityModel();

    ModelNode tqi;
    Map<String, ModelNode> qualityAspects = new HashMap<>();
    Map<String, ModelNode> productFactors = new HashMap<>();
    Map<String, ModelNode> measures = new HashMap<>();
    Map<String, ModelNode> diagnostics = new HashMap<>();

    JsonObject jsonQm;
    JsonObject jsonTqi;
    JsonObject jsonFactors;
    JsonObject jsonQualityAspects;
    JsonObject jsonProductFactors;
    JsonObject jsonMeasures;
    JsonObject jsonDiagnostics;


    // Constructor

    public QualityModelImport(Path qmFileLocation) {
        try {
            FileReader fr = new FileReader(qmFileLocation.toString());
            jsonQm = new JsonParser().parse(fr).getAsJsonObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    // Methods

    /**
     * One-shot run of model import.
     * <p>
     * Note: The procedures assume that each model node name is unique.
     * TODO (1.0): Pre-check that all model node names in the QM file are unique
     *
     * @return A PIQUE JVM object of the quality model file.
     */
    public QualityModel importQualityModel() {

        // Set top-level data
        qualityModel.setName(instanceNameFromJson());
        qualityModel.setBenchmarker(instanceBenchmarkerFromJson());
        qualityModel.setWeighter(instanceWeighterFromJson());

        // Instance each node, layer by layer (no edges yet)
        jsonFactors = jsonQm.getAsJsonObject("factors");
        jsonTqi = jsonFactors.getAsJsonObject("tqi");
        jsonQualityAspects = jsonFactors.getAsJsonObject("quality_aspects");
        jsonProductFactors = jsonFactors.getAsJsonObject("product_factors");
        jsonMeasures = jsonQm.getAsJsonObject("measures");
        jsonDiagnostics = jsonQm.getAsJsonObject("diagnostics");

        tqi = instanceTqiFromJson(jsonTqi);
        qualityAspects = instanceQualityAspectsFromJson(jsonQualityAspects);
        productFactors = instanceProductFactorsFromJson(jsonProductFactors);
        measures = instanceMeasuresFromJson(jsonMeasures);
        diagnostics = instanceDiagnosticsFromJson(jsonDiagnostics);

        // Use ModelNode instances to connect edges using name matching, bottom to top

        // Measures
        jsonMeasures.entrySet().forEach(this::connectNodeEdges);

        throw new NotImplementedException();
    }

    // TODO: Don't forget edge weights and utility functions for the derived model case
    private void connectNodeEdges(Map.Entry<String, JsonElement> rootNode) {
        String measureName = rootNode.getKey();
        JsonObject measureValues = rootNode.getValue().getAsJsonObject();

        // TODO PICKUP: Connect node edges: Parse string names of each "children" key for each measure values. Attach
        //  as ModelNode child using name and getNode value.
    }

    private IEvaluator getEvluatorFromConfiguration(JsonObject jsonQmNode, String nodeTypeQm) {
        if (jsonQmNode.get("eval_strategy") != null) {
            String fullClassName = jsonQmNode.get("eval_strategy").getAsString();
            try {
                return (IEvaluator) Class.forName(fullClassName).getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        else {
            switch (nodeTypeQm.toLowerCase()) {
                case "diagnostic":
                    return new DefaultDiagnosticEvaluator();
                case "factor":
                case "qualityaspect":
                case "quality_aspect":
                case "quality aspect":
                    return new DefaultFactorEvaluator();
                case "finding":
                    return new DefaultFindingEvaluator();
                case "measure":
                    return new DefaultMeasureEvaluator();
                case "productfactor":
                case "product factor":
                case "product_factor":
                    return new DefaultProductFactorEvaluator();
                default:
                    throw new RuntimeException("switch statement did not match nodeTypeQm parameter.");
            }
        }
    }

    private INormalizer getNormalizerFromConfiguration(JsonObject jsonQmNode) {
        if (jsonQmNode.get("normalizer") != null) {
            String fullClassName = jsonQmNode.get("normalizer").getAsString();
            try {
                return (INormalizer) Class.forName(fullClassName).getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        else {
            return new DefaultNormalizer();
        }
    }

    private IUtilityFunction getUtilityFunctionFromConfiguration(JsonObject jsonQmNode) {
        if (jsonQmNode.get("utility_function") != null) {
            String fullClassName = jsonQmNode.get("utility_function").getAsString();
            try {
                return (IUtilityFunction) Class.forName(fullClassName).getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        else {
            return new DefaultUtility();
        }
    }

    /**
     * Check for 'glocal_config' -> 'benchmark_strategy' and return the benchmark strategy class listed.
     * If configuration does not exist, return a {@link pique.calibration.NaiveBenchmarker}.
     */
    private IBenchmarker instanceBenchmarkerFromJson() {
        if (jsonQm.getAsJsonObject("global_config") != null && jsonQm.getAsJsonObject("global_config").get("benchmark_strategy") != null) {
            String fullClassName = jsonQm.getAsJsonObject("global_config").get("benchmark_strategy").getAsString();
            try {
                return (IBenchmarker) Class.forName(fullClassName).getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException();
            }
        } else {
            return new NaiveBenchmarker();
        }
    }

    /**
     * Example from measuresJson node:
         "diagnostics": {
            "TST0011": {
                "description": "Description for TST0011 diagnostic",
                "toolName": "Test tool"
            },
            "TST0012": {
                "description": "Description for TST0012 diagnostic",
                "toolName": "Test tool"
            }
         }
     */
    private Map<String, ModelNode> instanceDiagnosticsFromJson(JsonObject diagnosticsJson) {

        Map<String, ModelNode> tempDiagnostics = new HashMap<>();

        diagnosticsJson.entrySet().forEach(entry -> {

            JsonObject jsonDiagnostic = entry.getValue().getAsJsonObject();
            String diagnosticName = entry.getKey();
            String diagnosticDescription = jsonDiagnostic.get("description").getAsString();
            String diagnosticToolName = jsonDiagnostic.get("toolName").getAsString();
            IEvaluator evaluator = getEvluatorFromConfiguration(jsonDiagnostic, "diagnostic");
            INormalizer normalizer = getNormalizerFromConfiguration(jsonDiagnostic);
            IUtilityFunction utilityFunction = getUtilityFunctionFromConfiguration(jsonDiagnostic);

            // Instance the diagnostic
            Diagnostic d = new Diagnostic(diagnosticName, diagnosticDescription, diagnosticToolName,
                    evaluator, normalizer, utilityFunction);

            // Add to the collection
            if (tempDiagnostics.containsKey(d.getName())) {
                throw new RuntimeException("Two diagnostics with the same name were found while parsing the " +
                        "quality model file.");
            }
            tempDiagnostics.put(d.getName(), d);
        });

        return tempDiagnostics;
    }

    /**
     * Example from measuresJson node:
         "Measure 01": {
            "description": "Measure 01 description",
            "positive": false,
            "children": {
                "TST0011": {},
                "TST0012": {}
            }
         },
         "Measure 02": {
            "description": "Measure 02 description",
            "positive": false,
            "children": {
                "TST0021": {},
                "TST0022": {}
            }
         }
     */
    // TODO (1.0): Update to support any combination of non-default mechanisms
    private Map<String, ModelNode> instanceMeasuresFromJson(JsonObject measuresJson) {

        Map<String, ModelNode> tempMeasures = new HashMap<>();

        measuresJson.entrySet().forEach(entry -> {
            JsonObject jsonMeasure = entry.getValue().getAsJsonObject();
            String measureName = entry.getKey();
            String measureDescription = jsonMeasure.get("description").getAsString();
            boolean positive = jsonMeasure.getAsJsonPrimitive("positive").getAsBoolean();
            IEvaluator evaluator = getEvluatorFromConfiguration(jsonMeasure, "measure");
            INormalizer normalizer = getNormalizerFromConfiguration(jsonMeasure);
            IUtilityFunction utilityFunction = getUtilityFunctionFromConfiguration(jsonMeasure);

            // TODO (1.0): Support optional normalizer
            // Instance the measure
            Measure m = new Measure(measureName, measureDescription, evaluator, normalizer, utilityFunction, positive);

            // Optional thresholds
            Double[] thresholds = null;
            if (jsonMeasure.getAsJsonArray("thresholds") != null) {
                thresholds = new Double[jsonMeasure.getAsJsonArray("thresholds").size()];
                for (int i = 0; i < jsonMeasure.getAsJsonArray("thresholds").size(); i++) {
                    thresholds[i] = jsonMeasure.getAsJsonArray("thresholds").get(i).getAsDouble();
                }
                m.setThresholds(thresholds);
            }

            // Add to the collection
            if (tempMeasures.containsKey(m.getName())) {
                throw new RuntimeException("Two measures with the same name were found while parsing the " +
                        "quality model file.");
            }
            tempMeasures.put(m.getName(), m);
        });

        return tempMeasures;
    }

    private String instanceNameFromJson() {
        return jsonQm.getAsJsonPrimitive("name").getAsString();
    }

    /**
     * Example from from productFactorsJson node:
         "product_factors": {
            "ProductFactor 01": {
            "description": "ProductFactor 01 description",
            "children": {
                "Measure 01":{}
            }
         },
         "ProductFactor 02": {
            "description": "ProductFactor 02 description",
            "children": {
                "Measure 02":{}
            }
         }
     */
    // TODO (1.0): Update to support any combination of non-default mechanisms
    private Map<String, ModelNode> instanceProductFactorsFromJson(JsonObject productFactorsJson) {

        Map<String, ModelNode> tempProductFactors = new HashMap<>();

        productFactorsJson.entrySet().forEach(jsonProductFactor -> {

            JsonObject valueObj = jsonProductFactor.getValue().getAsJsonObject();
            String pfName = jsonProductFactor.getKey();
            String pfDescription = valueObj.get("description").getAsString();
            IEvaluator evaluator = getEvluatorFromConfiguration(valueObj, "productfactor");
            INormalizer normalizer = getNormalizerFromConfiguration(valueObj);
            IUtilityFunction utilityFunction = getUtilityFunctionFromConfiguration(valueObj);

            // Instance the product factor
            ProductFactor pf = new ProductFactor(pfName, pfDescription, evaluator, normalizer, utilityFunction);

            // Add to the collection
            if (tempProductFactors.containsKey(pf.getName())) {
                throw new RuntimeException("Two product factors with the same name were found while parsing the " +
                        "quality model file.");
            }
            tempProductFactors.put(pf.getName(), pf);
        });

        return tempProductFactors;
    }

    /**
     * Example from from qualityAspectsJson node:
     * <p>
     * "quality_aspects": {
     * "QualityAspect 01": {
     * "description": "QualityAspect 01 description"
     * },
     * "QualityAspect 02": {
     * "description": "QualityAspect 02 description"
     * }
     * ...
     * },
     */
    // TODO (1.0): Update to support any combination of non-default mechanisms
    private Map<String, ModelNode> instanceQualityAspectsFromJson(JsonObject qualityAspectsJson) {

        Map<String, ModelNode> tempQualityAspects = new HashMap<>();

        qualityAspectsJson.entrySet().forEach(entry -> {
            JsonObject valueObj = entry.getValue().getAsJsonObject();
            String qaName = entry.getKey();
            String qaDescription = valueObj.get("description").getAsString();
            IEvaluator evaluator = getEvluatorFromConfiguration(valueObj, "qualityaspect");
            INormalizer normalizer = getNormalizerFromConfiguration(valueObj);
            IUtilityFunction utilityFunction = getUtilityFunctionFromConfiguration(valueObj);

            // Instance the quality aspect
            QualityAspect qa = new QualityAspect(qaName, qaDescription, evaluator, normalizer, utilityFunction);

            // Add to the collection
            if (tempQualityAspects.containsKey(qa.getName())) {
                throw new RuntimeException("Two quality aspects with the same name were found while parsing the " +
                        "quality model file.");
            }
            tempQualityAspects.put(qa.getName(), qa);
        });

        return tempQualityAspects;
    }

    /**
     * Example format from tqiJson node:
     * <p>
     * "tqi": {
     * "Total Quality": {
     * "description": "Total Quality description"
     * }
     * },
     * ...
     */
    private ModelNode instanceTqiFromJson(JsonObject tqiJson) {

        Map.Entry<String, JsonElement> tqiEntry = tqiJson.entrySet().iterator().next();
        JsonObject tqiValues = tqiEntry.getValue().getAsJsonObject();

        String tqiName = tqiEntry.getKey();
        String tqiDescription = tqiValues.get("description").getAsString();
        IEvaluator evaluator = getEvluatorFromConfiguration(tqiValues, "factor");
        INormalizer normzlier = getNormalizerFromConfiguration(tqiValues);
        IUtilityFunction utilityFunction = getUtilityFunctionFromConfiguration(tqiValues);

        return new Tqi(tqiName, tqiDescription, null, evaluator, normzlier, utilityFunction);
    }

    /**
     * Check for 'glocal_config' -> 'weights_strategy' and return the weighter strategy class listed.
     * If configuration does not exist, return a {@link pique.calibration.NaiveWeighter}.
     */
    private IWeighter instanceWeighterFromJson() {
        if (jsonQm.getAsJsonObject("global_config") != null && jsonQm.getAsJsonObject("global_config").get("weights_strategy") != null) {
            String fullClassName = jsonQm.getAsJsonObject("global_config").get("weights_strategy").getAsString();
            try {
                return (IWeighter) Class.forName(fullClassName).getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException();
            }
        } else {
            return new NaiveWeighter();
        }
    }

}
