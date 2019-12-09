package qatch.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import qatch.analysis.Diagnostic;
import qatch.analysis.Finding;
import qatch.analysis.Measure;

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
	 * Constructor for deriving QM object from a disk file description (likely .json or .xml)
	 * @param qmFilePath
	 * 		File path to the quality model description
	 */
	public QualityModel(Path qmFilePath) {
		importQualityModel(qmFilePath);
	}

	/**
	 * Constructor for use in deep cloning this quality model
	 *
	 * @param name
	 * 		Quality model name
	 * @param tqi
	 * 		Root node of quality model tree. Careful passing this by reference.
	 * 		Will likely want to use this.tqi.clone().
	 */
	public QualityModel(String name, Tqi tqi) {
		this.name = name;
		this.tqi = tqi;
	}


	// Setters and Getters
	public Characteristic getAnyCharacteristic() {
		return getTqi().getAnyCharacteristic();
	}
	public Property getAnyProperty() {
		Property anyProperty = getAnyCharacteristic().getProperties().values().stream().findAny().orElse(null);
		assert anyProperty != null;
		return anyProperty;
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
	public void setFinding(Measure diagnosticsMeasure, String diagnosticName, Finding finding) {
		diagnosticsMeasure.getDiagnostic(diagnosticName).setFinding(finding);
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
	@Override
	public QualityModel clone() {
		Tqi rootNode = (Tqi)getTqi().clone();
		return new QualityModel(getName(), rootNode);
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof QualityModel)) { return false; }
		QualityModel otherQm = (QualityModel) other;

		return getName().equals(otherQm.getName())
				&& getTqi().equals(otherQm.getTqi());
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
		QualityModelExport qmExport = new QualityModelExport(this);
		return qmExport.exportToJson(fileName, outputDirectory);
	}

	/**
	 * This method is responsible for importing the desired Quality Model
	 * by parsing the file that contains the text data of the Quality Model.
	 */
	private void importQualityModel(Path qmFilePath) {

		// TODO: assert well-formed quality model json file

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
			JsonObject jsonCharacteristics = jsonQm.getAsJsonObject("characteristics");
			jsonCharacteristics.keySet().forEach(key -> {
				JsonObject currentCharacteristic = jsonCharacteristics.getAsJsonObject(key);
				String name = currentCharacteristic.getAsJsonPrimitive("name").getAsString();
				String description = currentCharacteristic.getAsJsonPrimitive("description").getAsString();
				Characteristic qmCharacteristic = new Characteristic(name, description);
				if (currentCharacteristic.getAsJsonObject("weights") != null) {
					currentCharacteristic.getAsJsonObject("weights").keySet().forEach(weight -> {
						qmCharacteristic.setWeight(weight, currentCharacteristic.getAsJsonObject("weights").getAsJsonPrimitive(weight).getAsDouble());
					});
				}

				setCharacteristic(qmCharacteristic.getName(), qmCharacteristic);
			});

			// Properties nodes
			JsonObject jsonProperties = jsonQm.getAsJsonObject("properties");
			jsonProperties.keySet().forEach(key -> {
				JsonObject currentProperty = jsonProperties.getAsJsonObject(key);
				String name = currentProperty.getAsJsonPrimitive("name").getAsString();
				String description = currentProperty.getAsJsonPrimitive("description").getAsString();
				boolean impact = currentProperty.getAsJsonPrimitive("positive").getAsBoolean();
				Double[] thresholds = new Double[3];

				if (currentProperty.getAsJsonArray("thresholds") != null) {
					JsonArray jsonThresholds = currentProperty.getAsJsonArray("thresholds");
					for (int i = 0; i < jsonThresholds.size(); i++) {
						thresholds[i] = jsonThresholds.get(i).getAsDouble();
					}
				}

				// Property's Measure and Diagnostics nodes
				JsonObject jsonMeasure = currentProperty.getAsJsonObject("measure");
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

			// TODO: Assert that weight mappings have correct Property and Characteristics to map to and are pass by reference for characteristics (make new method)

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
