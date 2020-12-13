package pique.model;

import com.google.gson.annotations.Expose;
import org.apache.commons.lang3.tuple.Pair;
import pique.utility.FileUtility;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Duplicate information of the QualityModel class, but with fields set for ease of Gson json exporting.
 * This class is a cheap hack for exporting a quality model in the desired format. This class is esentially
 * a container class.
 *
 * There is a much better way to handle this instead of having this class, but that can be a future problem to deal with :)
 *
 * @author Rice
 */
// TODO (1.0): Better GSON supprt.  Expose while limiting depth. Currect state will cause problems for large quality
//  models
public class QualityModelExport {

    /// Fields ///
    @Expose
    private String name;
    @Expose
    private Map<String, String> additionalData = new HashMap<>();
    @Expose
    private Map<String, String> global_config = new HashMap<>();
    @Expose
    private Map<String, Map<String, ModelNode>> factors = new HashMap<>();
    @Expose
    private Map<String, ModelNode> measures = new HashMap<>();


    /// Constructor
    @SafeVarargs
    public QualityModelExport(QualityModel qualityModel, Pair<String, String>... optional) {

        // Setup
        getFactors().put("tqi", new HashMap<>());
        getFactors().put("quality_aspects", new HashMap<>());
        getFactors().put("product_factors", new HashMap<>());

        // Basic info
        this.name = qualityModel.getName();
        this.global_config.put("benchmark_strategy", qualityModel.getBenchmarker().getName());
        this.global_config.put("weights_strategy", qualityModel.getWeighter().getName());
        if (optional.length > 0 ) { for (Pair<String, String> entry : optional) { additionalData.put(entry.getKey(), entry.getValue()); }}

        // factors -> tqi
        Tqi tqi = qualityModel.getTqi();
        getFactors().get("tqi").put(tqi.getName(), tqi);

        // factors -> quality_aspects
        qualityModel.getQualityAspects().values().forEach(qualityAspect -> getFactors().get("quality_aspects").put(qualityAspect.getName(), qualityAspect));

        // factors -> product_factors
        qualityModel.getProductFactors().values().forEach(productFactor -> getFactors().get("product_factors").put(productFactor.getName(), productFactor));

        // measures
        qualityModel.getMeasures().values().forEach(measure -> getMeasures().put(measure.getName(), measure));
    }



    /// Getters and setters ///
    public Map<String, String> getAdditionalData() {
        return additionalData;
    }

    public Map<String, Map<String, ModelNode>> getFactors() {
        return factors;
    }

    public Map<String, ModelNode> getMeasures() {
        return measures;
    }

    public String getName() {
        return name;
    }


    /// Methods ///
    /**
     * Create a hard-drive file representation of the model
     *
     * @param outputDirectory
     * 		The directory to place the QM file into.  Does not need to exist beforehand.
     * @return
     * 		The path of the exported model file.
     */
    public Path exportToJson(String fileName, Path outputDirectory) {
        return FileUtility.exportObjectToJson(this, outputDirectory, fileName);
    }
}
