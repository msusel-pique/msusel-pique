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


	// constructor
	public QualityModelLoader(Path qmFilePath){
		this.qmFilePath = qmFilePath;
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
			FileReader fr = new FileReader(qmFilePath.toString());
			JsonObject jsonQm = new JsonParser().parse(fr).getAsJsonObject();
			fr.close();

			// name
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
				qmMeasure.setToolName(jsonMeasure.getAsJsonPrimitive("tool").getAsString());
				qmMeasure.setDiagnostics(qmDiagnostics);

				Property qmProperty = new Property(name, description, impact, thresholds, qmMeasure);

				qm.setProperty(qmProperty.getName(), qmProperty);
			});
		} catch (IOException e) {
			e.printStackTrace();
		}

		return qm;
	}
}
