package qatch.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

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
    private ArrayList<Characteristic> characteristics;
    @Expose
    private ArrayList<Property> properties;

    public QualityModelExport(QualityModel qualityModel) {
        this.name = qualityModel.getName();
        this.tqi = qualityModel.getTqi();
        this.characteristics = (ArrayList<Characteristic>) qualityModel.getCharacteristics().values();
        this.properties = (ArrayList<Property>) qualityModel.getProperties().values();
    }

    // TODO PICKUP: quality model export test, then integrate into QualityModel.exportToJson()
}
