package qatch.model;

import com.google.gson.annotations.Expose;
import org.apache.commons.lang3.tuple.Pair;
import qatch.utility.FileUtility;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Duplicate information of the QualityModel class, but with fields set for ease of Gson json exporting.
 * This class is a cheap hack for exporting a quality model in the desired format. This class is esentially
 * a container class.
 *
 * There is a much better way to handle this instead of having this class, but that can be a future
 * developer's problem :)
 *
 * @author Rice
 */
public class QualityModelExport {

    // Fields

    @Expose
    private String name;
    @Expose
    private Map<String, String> additionalData = new HashMap<>();
    @Expose
    private Tqi tqi;
    @Expose
    private Map<String, Characteristic>  characteristics;
    @Expose
    private Map<String, Property> properties;

    @SafeVarargs
    public QualityModelExport(QualityModel qualityModel, Pair<String, String>... optional) {
        this.name = qualityModel.getName();
        this.tqi = qualityModel.getTqi();
        this.characteristics = qualityModel.getCharacteristics();
        this.properties = qualityModel.getProperties();
        if (optional.length > 0 ) {
            for (Pair<String, String> entry : optional) {
                additionalData.put(entry.getKey(), entry.getValue());
            }
        }
    }

    public Map<String, String> getAdditionalData() {
        return additionalData;
    }
    public Map<String, Characteristic> getCharacteristics() {
        return characteristics;
    }
    public String getName() {
        return name;
    }
    public Map<String, Property> getProperties() {
        return properties;
    }
    public Tqi getTqi() {
        return tqi;
    }


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
