package qatch.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import qatch.analysis.Diagnostic;
import qatch.analysis.Measure;
import qatch.utility.FileUtility;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

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
 */
/*
 * TODO:
 *   (1) Add checks immediately after model creation that no duplicate node names by-level exist
 */
public class QualityModel {

	// Fields
	@Expose
	private String name;  //The name of the QM found in the XML file
	@Expose
	private Tqi tqi;  // root node, the total quality evaluation, contains characteristic objects as children

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
	private Characteristic getAnyCharacteristic() {
		Characteristic anyCharacteristic = getTqi().getCharacteristics().values().stream().findAny().orElse(null);
		assert anyCharacteristic != null;
		return anyCharacteristic;
	}
	public Characteristic getCharacteristic(String name) {
		return getTqi().getCharacteristics().get(name);
	}
	public Map<String, Characteristic> getCharacteristics() {
		return getTqi().getCharacteristics();
	}
	public void setCharacteristic(String characteristicName, Characteristic characteristic) {
		getTqi().getCharacteristics().put(characteristicName, characteristic);
	}
	public void setCharacteristics(Map<String, Characteristic> characteristics) {
		getTqi().setCharacteristics(characteristics);
	}
	public Measure getMeasure(String measureName) {
		for (Property property : getProperties().values()) {
			if (property.getMeasure().getName().equals(measureName)) {
				return property.getMeasure();
			}
		}
		throw new RuntimeException("Unable to find measure with name " + measureName + " from QM's property nodes");
	}
	public Map<String, Measure> getMeasures() {
		Map<String, Measure> measures = new HashMap<>();
		List<Property> propertyList = new ArrayList<>(getProperties().values());
		propertyList.forEach(property -> {
			measures.put(property.getMeasure().getName(), property.getMeasure());
		});
		return measures;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Property getProperty(String name) {
		return getProperties().get(name);
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
		return getAnyCharacteristic().getProperties();
	}
	public void setProperty(Property property) {
		Set<Characteristic> allCharacteristics = new HashSet<>(getTqi().getCharacteristics().values());
		allCharacteristics.forEach(characteristic -> {
			characteristic.setProperty(property);
		});
	}
	public void setProperties(Map<String, Property> properties) {
		Set<Characteristic> allCharacteristics = new HashSet<>(getTqi().getCharacteristics().values());
		allCharacteristics.forEach(characteristic -> {
			characteristic.setProperties(properties);
		});
	}
	public Tqi getTqi() {
		return tqi;
	}
	public void setTqi(Tqi tqi) {
		this.tqi = tqi;
	}


	// Methods

	/**
	 * @return
	 * 		Deep clone of this QualityModel object
	 */
	public QualityModel clone() {

		throw new NotImplementedException();
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
				Characteristic qmCharacteristic = new Characteristic(name, description);
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
					String dName = d.getAsJsonObject().get("name").getAsString();
					String dDescription = d.getAsJsonObject().get("description").getAsString();
					String dToolName = d.getAsJsonObject().get("toolName").getAsString();
					qmDiagnostics.add(new Diagnostic(dName, dDescription, dToolName));
				});

				qmMeasure.setName(jsonMeasure.getAsJsonPrimitive("name").getAsString());
				qmMeasure.setDiagnostics(qmDiagnostics);

				Property qmProperty = new Property(name, description, impact, thresholds, qmMeasure);

				setProperty(qmProperty);
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
