package qatch.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import qatch.analysis.Diagnostic;
import qatch.analysis.Measure;
import qatch.utility.FileUtility;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class encapsulates all the appropriate information that describe a 
 * Quality Model that can be used for the evaluation of a single project or 
 * a benchmark of projects.
 * 
 * Typically, it is used in order to load the PropertySet and the CharacteristicSet
 * of the XML file that describes the quality model and assign their values to the 
 * project (or projects) that we want to evaluate.
 * 
 * @author Miltos, Rice
 *
 */
/*
 * TODO:
 *   (1) Add checks immediately after model creation that no duplicate node names by-level exist
 */
public class QualityModel {

	// Fields
	private String name;  //The name of the QM found in the XML file
	private Tqi tqi;  // root node, the total quality evaluation, contains characteristic objects as children
	private Map<String, Characteristic> characteristics = new HashMap<>();
	private Map<String, Property> properties = new HashMap<>();  // each property has one Measure associated with it
	private CharacteristicSet characteristics_deprecated;  //The CharacteristicSet containing all the characteristics_deprecated of the Quality Model
	private PropertySet properties_deprecated;			   //The PropertySet containing all the properties of the Quality Model


	// Constructors

	/**
	 * Instantiate a QM object with no data or config. Likely only use this constructor for testing
	 * @param name
	 * 		Name of the quality model
	 */
	public QualityModel(String name) {
		System.out.println("--- WARNING ---\n" +
				"You are calling QualityModel with a String input, this should only be used for testing purposes.");
		this.name = name;
	}

	/**
	 * Constructor for deriving QM object from a disk file description (likely .json or .xml)
	 * @param qmFilePath
	 * 		File path to the quality model description
	 */
	public QualityModel(Path qmFilePath) {
		importQualityModel(qmFilePath);
	}


	// Setters and Getters

	public Characteristic getCharacteristic(String name) {
		return this.characteristics.get(name);
	}
	public Map<String, Characteristic> getCharacteristics() {
		return characteristics;
	}
	public void setCharacteristic(String characteristicName, Characteristic characteristic) {
		this.characteristics.put(characteristicName, characteristic);
	}
	public void setCharacteristics(Map<String, Characteristic> characteristics) {
		this.characteristics = characteristics;
	}
	public Measure getMeasure(String measureName) {
		for (Property property : getProperties().values()) {
			if (property.getMeasure().getName().equals(measureName)) {
				return property.getMeasure();
			}
		}
		throw new RuntimeException("Unable to find measure with name " + measureName + " from QM's property nodes");
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Property getProperty(String name) {
		return this.properties.get(name);
	}
	public Property getPropertyByMeasureName(String measureName) {
		for (Property property : getProperties().values()) {
			if (property.getMeasure().getName().equals(measureName)) {
				return property;
			}
		}
		throw new RuntimeException("Unable to find property with child measure name " + measureName + " from QM's property nodes");
	}
	public Map<String, Property> getProperties() {
		return properties;
	}
	public void setProperty(String propertyName, Property property) {
		this.properties.put(propertyName, property);
	}
	public void setProperties(Map<String, Property> properties) {
		this.properties = properties;
	}
	public Tqi getTqi() {
		return tqi;
	}
	public void setTqi(Tqi tqi) {
		this.tqi = tqi;
	}
	public PropertySet getProperties_deprecated() {
		return properties_deprecated;
	}
	public CharacteristicSet getCharacteristics_deprecated() {
		return characteristics_deprecated;
	}
	public void setCharacteristics_deprecated(CharacteristicSet characteristics_deprecated) {
		this.characteristics_deprecated = characteristics_deprecated;
	}


	// Methods
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

	/**
	 * This method is responsible for importing the desired Quality Model
	 * by parsing the file that contains the text data of the Quality Model.
	 */
	private void importQualityModel(Path qmFilePath) {
		// Parse json data and update quality model object
		try {
			// TODO: break this large method into smaller method calls
			FileReader fr = new FileReader(qmFilePath.toString());
			JsonObject jsonQm = new JsonParser().parse(fr).getAsJsonObject();
			fr.close();

			// Name
			setName(jsonQm.getAsJsonPrimitive("name").getAsString());

			// Root node
			JsonObject jsonTqi = jsonQm.getAsJsonObject("tqi");
			setTqi(new Tqi(null, null, null));
			getTqi().setName(jsonTqi.getAsJsonPrimitive("name").getAsString());
			if (jsonTqi.getAsJsonObject("weights") != null) {
				jsonTqi.getAsJsonObject("weights").keySet().forEach(weight -> {
					getTqi().setWeight(weight, jsonTqi.getAsJsonObject("weights").getAsJsonPrimitive(weight).getAsDouble());
				});
			}

			// Characteristics nodes
			JsonArray jsonCharacteristics = jsonQm.getAsJsonArray("characteristics");
			jsonCharacteristics.forEach(c -> {
				JsonObject jsonCharacteristic = c.getAsJsonObject();

				String name = jsonCharacteristic.getAsJsonPrimitive("name").getAsString();
				String standard = jsonCharacteristic.getAsJsonPrimitive("standard").getAsString();
				String description = jsonCharacteristic.getAsJsonPrimitive("description").getAsString();
				Characteristic qmCharacteristic = new Characteristic(name, description, standard);
				if (jsonCharacteristic.getAsJsonObject("weights") != null) {
					jsonCharacteristic.getAsJsonObject("weights").keySet().forEach(weight -> {
						qmCharacteristic.setWeight(weight, jsonCharacteristic.getAsJsonObject("weights").getAsJsonPrimitive(weight).getAsDouble());
					});
				}

				setCharacteristic(qmCharacteristic.getName(), qmCharacteristic);
			});

			// Properties nodes
			JsonArray jsonProperties = jsonQm.getAsJsonArray("properties");

			jsonProperties.forEach(p -> {
				JsonObject jsonProperty = p.getAsJsonObject();
				String name = jsonProperty.getAsJsonPrimitive("name").getAsString();
				String description = jsonProperty.getAsJsonPrimitive("description").getAsString();
				boolean impact = jsonProperty.getAsJsonPrimitive("positive_impact").getAsBoolean();
				Double[] thresholds = new Double[3];

				if (jsonProperty.getAsJsonArray("thresholds") != null) {
					JsonArray jsonThresholds = jsonProperty.getAsJsonArray("thresholds");
					for (int i = 0; i < jsonThresholds.size(); i++) {
						thresholds[i] = jsonThresholds.get(i).getAsDouble();
					}
				}

				JsonObject jsonMeasure = jsonProperty.getAsJsonObject("measure");
				JsonArray jsonDiagnostics = jsonMeasure.getAsJsonArray("diagnostics");
				Measure qmMeasure = new Measure();
				List<Diagnostic> qmDiagnostics = new ArrayList<>();
				jsonDiagnostics.forEach(d -> {
					qmDiagnostics.add(new Diagnostic(d.getAsString()));
				});

				qmMeasure.setName(jsonMeasure.getAsJsonPrimitive("name").getAsString());
				qmMeasure.setToolName(jsonMeasure.getAsJsonPrimitive("tool").getAsString());
				qmMeasure.setDiagnostics(qmDiagnostics);

				Property qmProperty = new Property(name, description, impact, thresholds, qmMeasure);

				setProperty(qmProperty.getName(), qmProperty);
			});

			// Construct tree structure with TQI as root node
			getTqi().setCharacteristics(getCharacteristics());
			getTqi().getCharacteristics().values().forEach(c -> c.setProperties(getProperties()));

			// TODO: Assert that weight mappings have correct Property and Characteristics to map to (make new method)

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
