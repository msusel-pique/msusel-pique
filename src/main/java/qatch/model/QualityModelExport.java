package qatch.model;

import com.google.gson.annotations.Expose;
import qatch.utility.FileUtility;

import java.nio.file.Path;
import java.util.Map;

/**
 * Duplicate information of the QualityModel class, but with fields set for ease of Gson json exporting.
 * This class is a cheap hack for exporting a quality model in the desired format.
 * This class is esentially a container class.
 * There is a much better way to handle this instead of having this class, but that can be a future
 * developer's problem :)
 *
 * @author David Rice
 */
public class QualityModelExport {

    // Fields
    @Expose
    private String name;
    @Expose
    private Tqi tqi;
    @Expose
    private Map<String, Characteristic>  characteristics;
    @Expose
    private Map<String, Property> properties;

    public QualityModelExport(QualityModel qualityModel) {
        this.name = qualityModel.getName();
        this.tqi = qualityModel.getTqi();
        this.characteristics = qualityModel.getCharacteristics();
        this.properties = qualityModel.getProperties();
    }

    // TODO PICKUP: quality model export test, then integrate into QualityModel.exportToJson()

    public String getName() {
        return name;
    }
    public Tqi getTqi() {
        return tqi;
    }
    public Map<String, Characteristic> getCharacteristics() {
        return characteristics;
    }
    public Map<String, Property> getProperties() {
        return properties;
    }

    /**
     * Create a hard-drive file representation of the model
     *
     * @param outputDirectory
     * 		The directory to place the QM file into.  Does not need to exist beforehand.
     * @return
     * 		The path of the exported model file.
     */
    public Path exportToJson(Path outputDirectory) {
        String fileName = "qualityModel_" + getName().replaceAll("\\s","");
        return FileUtility.exportObjectToJson(this, outputDirectory, fileName);
    }
}
