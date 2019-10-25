package qatch.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jdom.Element;
import qatch.analysis.Diagnostic;
import qatch.analysis.Measure;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for loading the Quality Model from 
 * the appropriate quality model description XML file.
 * 
 * @author Miltos
 *
 *TODO: Remove the elements <weights> and <thresholds> from the XML file.
 *		Place the <weight> and <threshold> elements directly inside 
 *      characteristic and property nodes.
 */
public class QualityModelLoader {

	// instance variables
	private Path qmFilePath;  //The exact path where the file that contains the QM description is placed


	//Constructor
	public QualityModelLoader(Path qmFilePath){
		this.qmFilePath = qmFilePath;
	}


	//Setters and Getters
	public Path getQmFilePath() {
		return qmFilePath;
	}


	// methods
	/**
	 * This method is responsible for importing the desired Quality Model
	 * by parsing the file that contains the description of the Quality
	 * Model.
	 * 
	 * Typically, this method:
	 * 	1. Creates an empty QualityModel object
	 *  2. Fetches the basic elements of the xml file (i.e. tqi, characteristics, properties)
	 *  3. Passes each element to the appropriate build-in method that is 
	 *     responsible for the parsing of a specific node
	 *  4. Sets the fields of the QualityModel object to the values returned
	 *     by these methods.
	 *  5. Returns the QualityModel object
	 *  
	 * @return : A QualityModel object containing the object oriented representation 
	 *          of the desired quality model.
	 */
	public QualityModel importQualityModel(){
		//Create an empty QualityModel Object
		QualityModel qm = new QualityModel();

		// parse json data and update quality model object
		try {
			JsonObject jsonQm = new JsonParser().parse(new FileReader(qmFilePath.toString())).getAsJsonObject();
			qm.setName(jsonQm.getAsJsonPrimitive("name").getAsString());

			// root node
			JsonObject jsonTqi = jsonQm.getAsJsonObject("tqi");
			Tqi qmTqi = qm.getTqi();
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

				qm.setCharacteristic(qmCharacteristic.getName(), qmCharacteristic);
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
				qmMeasure.setTool(jsonMeasure.getAsJsonPrimitive("tool").getAsString());
				qmMeasure.setDiagnostics(qmDiagnostics);

				Property qmProperty = new Property(name, description, impact, thresholds, qmMeasure);

				qm.setProperty(qmProperty.getName(), qmProperty);
			});
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return qm;
	}
	
	/**
	 * This method parses the <tqi> node of the quality model XML file 
	 * and returns a Tqi object that contains the parsed information.
	 */
	public static Tqi loadTqiNode(Element tqiNode){
		
		//Create a new Tqi object
		Tqi tqi = new Tqi();
		
		//Create a list with the weight nodes of the tqi node
		List<Element> weightsNode = tqiNode.getChildren();
		List<Element> weights = weightsNode.get(0).getChildren();
		
		//For each <weight> node do...
		for(Element weight : weights){
			tqi.addWeight(Double.parseDouble(weight.getText()));
		}
		
		//Return the tqi object
		return tqi;
	}
	
	
	/**
	 * This method parses the <characteristics> node of the quality model XML file 
	 * and returns a CharacteristicSet object that contains all the characteristics
	 * found in the XML file.
	 */
	public static CharacteristicSet loadCharacteristicsNode(Element characteristicsNode){
		
		//Create a new CharacteristicSet object
		CharacteristicSet characteristics = new CharacteristicSet();
		
		//Get a list with all the characteristic nodes of the xml file
		List<Element> charNodes = characteristicsNode.getChildren();
		
		//For each characteristic node do ...
		for(Element charNode : charNodes){
			
			//Create a new Characteristic object
			Characteristic characteristic = new Characteristic();
			
			//Set the metadata of the current characteristic
			characteristic.setName(charNode.getAttributeValue("name"));
			characteristic.setStandard(charNode.getAttributeValue("standard"));
			characteristic.setDescription(charNode.getAttributeValue("description"));
			
			//Get the list of the characteristic's weights
			//TODO: Remove the <weights> node from the xml - list the weights directly inside <characteristic> element
			List<Element> weightsNode = charNode.getChildren();
			List<Element> weights = weightsNode.get(0).getChildren();
			//For each weight node do ...
			for(Element weight : weights){
				//Get the current weight and add it to the characteristic
				characteristic.addWeight(Double.parseDouble(weight.getText()));
			}
			
			//Add the characteristic to the CharacteristicSet
			characteristics.addCharacteristic(characteristic);
		}
		
		//Return the characteristics of the quality model
		return characteristics;
	}
	
	/**
	 * This method parses the <properties> node of the quality model XML file 
	 * and returns a PropertySet object that contains all the properties
	 * found in the XML file.
	 */
	public static PropertySet loadPropertiesNode(Element propertiesNode){
		
		//Create a new PropertySet object
		PropertySet properties = new PropertySet();
		
		//Get a list with all the characteristic nodes of the xml file
		List<Element> propNodes = propertiesNode.getChildren();
		
		//For each property node do ...
		for(Element propNode : propNodes){
			//Create a new Property object
			Property property = new Property();
			
			//Set the metadata of the current characteristic
			property.setName(propNode.getAttributeValue("name"));
			property.setPositive(Boolean.parseBoolean(propNode.getAttributeValue("positive_impact")));
			property.setDescription(propNode.getAttributeValue("description"));
			property.getMeasure().setMetricName(propNode.getAttributeValue("metricName"));
			property.getMeasure().setRulesetPath(propNode.getAttributeValue("ruleset"));
			property.getMeasure().setType(Integer.parseInt(propNode.getAttributeValue("type")));
			property.getMeasure().setTool(propNode.getAttributeValue("tool"));
			
			//Get the thresholds of the property
			//Get the list of the property's thresholds
			//TODO: Remove the <thresholds> node from the xml - list the thresholds directly inside <threshold> element
			List<Element> thresholdsNode = propNode.getChildren();
			List<Element> thresholds = thresholdsNode.get(0).getChildren();
			
			//For each threshold node do ...
			int i = 0;
			for(Element threshold : thresholds){
				//Get the current threshold and add it to the property
				property.getThresholds()[i] = Double.parseDouble(threshold.getText());
				i++;
			}
			
			//Add the property to the PropertySet
			properties.addProperty(property);
			
		}
		
		//Return the properties of the quality model
		return properties;
	}

}
