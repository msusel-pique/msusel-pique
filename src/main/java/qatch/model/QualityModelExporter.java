package qatch.model;

import com.google.gson.Gson;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

/**
 * This class is responsible for the exportation of the XML file that
 * represents the Quality Model, as a result of calibration and weight
 * elicitation.
 * 
 * Typically, it combines the three individual exporters in order to 
 * export all the information concerning the Quality Model, into a single 
 * XML or JSON file.
 * 
 * @author Miltos
 *
 */
public class QualityModelExporter {
	
	/**
	 * A method that creates the JDOM representation of the Quality Model and exports
	 * it in XML format, recognizable by the Evaluator subsystem.
	 *
	 * @param qualityModel
	 * 		The QualityModel object. By this state, the QM object's TQI and Characteristics should have
	 * 		elicited weight values and the Properties should have elicited threshold values
	 * @param xmlPath
	 * 		The path representation of the directory to place the quality_model.xml file in.
	 * 		The directory structure does not need to fully exist yet.
	 */
	public void exportQualityModelToXML(QualityModel qualityModel, Path xmlPath){

		// build directory structure if needed
		xmlPath.toFile().mkdirs();
		File qmOut = new File(xmlPath.toFile(), "qualityModel.xml");

		//Create the individual exporters
		PropertiesExporter prExp = new PropertiesExporter();
		CharacteristicsExporter charExp = new CharacteristicsExporter();
		TqiExporter tqiExp = new TqiExporter();
		
		//Retrieve the JDOM Representation of each Quality Model's node
		Element tqiRoot = tqiExp.createJDOMRepresentation(qualityModel.getTqi());
		Element charRoot = charExp.createJDOMRepresentation(qualityModel.getCharacteristics());
		Element propRoot = prExp.createJDOMRepresentation(qualityModel.getProperties());
		
		//Create the root of the Quality Model XML file
		Element root = new Element("quality_model");
		root.setAttribute("name", qualityModel.getName());
		
		//Add the three elements to the root element
		root.addContent(tqiRoot);
		root.addContent(charRoot);
		root.addContent(propRoot);
		
		//Export the QualityModel
		try {
			//Create an XML Outputter
			XMLOutputter outputter = new XMLOutputter();
			
			//Set the format of the outputted XML File
			Format format = Format.getPrettyFormat();
			outputter.setFormat(format);
			
			//Output the XML File to standard output and the desired file
			FileWriter filew = new FileWriter(qmOut);
			outputter.output(root, filew);
			filew.close();
			
		}
		catch (IOException e){ System.out.println(e.getMessage()); }
	}

	/**
	 * A method that exports the Quality Model in JSON format.
	 */
	public void exportQualityModelToJSON(QualityModel qualityModel, Path jsonPath){
		//Create a Gson json parser
		Gson gson = new Gson();

		//Parse your tqi into a json String representation
		String json = gson.toJson(qualityModel);

		try{
			FileWriter writer = new FileWriter(jsonPath.toFile());
			writer.write(json);
			writer.close();
		}
		catch(IOException e){ System.out.println(e.getMessage()); }
	}

}
