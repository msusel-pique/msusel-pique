package qatch.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import qatch.analysis.Diagnostic;
import qatch.analysis.Measure;

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
 * @author Miltos
 *
 */
public class QualityModel {

	// Fields
	private String name;  //The name of the QM found in the XML file
	private Tqi tqi = new Tqi();  // root node, the total quality evaluation, contains characteristic objects as children
	private Map<String, Characteristic> characteristics = new HashMap<>();
	private Map<String, Property> properties = new HashMap<>();  // each property has one Measure associated with it
	private CharacteristicSet characteristics_deprecated;  //The CharacteristicSet containing all the characteristics_deprecated of the Quality Model
	private PropertySet properties_deprecated;			   //The PropertySet containing all the properties of the Quality Model


	// Constructor
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

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Property getProperty(String name) {
		return this.properties.get(name);
	}
	public Map<String, Property> getProperties() {
		return properties;
	}
	public void setProperty(String propertyName, Property property) {
		this.properties.put(propertyName, property);
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
	public void setProperties_deprecated(PropertySet properties_deprecated) {
		this.properties_deprecated = properties_deprecated;
	}

	public CharacteristicSet getCharacteristics_deprecated() {
		return characteristics_deprecated;
	}
	public void setCharacteristics_deprecated(CharacteristicSet characteristics_deprecated) {
		this.characteristics_deprecated = characteristics_deprecated;
	}


	// Methods
	/**
	 * This method is responsible for importing the desired Quality Model
	 * by parsing the file that contains the text data of the Quality Model.
	 */
	private void importQualityModel(Path qmFilePath) {
		// parse json data and update quality model object
		try {
			FileReader fr = new FileReader(qmFilePath.toString());
			JsonObject jsonQm = new JsonParser().parse(fr).getAsJsonObject();
			fr.close();

			// name
			setName(jsonQm.getAsJsonPrimitive("name").getAsString());

			// root node
			JsonObject jsonTqi = jsonQm.getAsJsonObject("tqi");
			Tqi qmTqi = getTqi();
			qmTqi.setName(jsonTqi.getAsJsonPrimitive("name").getAsString());
			jsonTqi.getAsJsonObject("weights").keySet().forEach(weight -> {
				qmTqi.setWeight(weight, jsonTqi.getAsJsonObject("weights").getAsJsonPrimitive(weight).getAsDouble());
			});

			// characteristics nodes
			JsonArray jsonCharacteristics = jsonQm.getAsJsonArray("characteristics");
			jsonCharacteristics.forEach(c -> {
				JsonObject jsonCharacteristic = c.getAsJsonObject();

				String name = jsonCharacteristic.getAsJsonPrimitive("name").getAsString();
				String standard = jsonCharacteristic.getAsJsonPrimitive("standard").getAsString();
				String description = jsonCharacteristic.getAsJsonPrimitive("description").getAsString();
				Characteristic qmCharacteristic = new Characteristic(name, standard, description);

				jsonCharacteristic.getAsJsonObject("weights").keySet().forEach(weight -> {
					qmCharacteristic.setWeight(weight, jsonCharacteristic.getAsJsonObject("weights").getAsJsonPrimitive(weight).getAsDouble());
				});

				setCharacteristic(qmCharacteristic.getName(), qmCharacteristic);
			});

			// properties nodes
			JsonArray jsonProperties = jsonQm.getAsJsonArray("properties");

			jsonProperties.forEach(p -> {
				JsonObject jsonProperty = p.getAsJsonObject();
				JsonArray jsonThresholds = jsonProperty.getAsJsonArray("thresholds");

				String name = jsonProperty.getAsJsonPrimitive("name").getAsString();
				String description = jsonProperty.getAsJsonPrimitive("description").getAsString();
				boolean impact = jsonProperty.getAsJsonPrimitive("positive_impact").getAsBoolean();
				double[] thresholds = new double[jsonThresholds.size()];
				for (int i = 0; i < jsonThresholds.size(); i++) {
					thresholds[i] = jsonThresholds.get(i).getAsDouble();
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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
