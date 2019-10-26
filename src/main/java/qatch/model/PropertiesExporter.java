package qatch.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.google.gson.Gson;

/**
 * This class is responsible for exporting the properties of a PropertySet
 * into different formats (e.g. XML, JSON etc.). It is used primarily 
 * in order to export the properties of the Quality Model into different 
 * formats for debugging purposes.
 * 
 * Basically, it is used for exporting the DOM Tree representation of the
 * property set that, together with the DOM Tree representation of the 
 * CharacteristicSet, contracts the Quality Model XML file.
 * 
 * @author Miltos
 *
 */
@Deprecated
public class PropertiesExporter {
	
	//Predefined - recommended paths for storing the XML and JSON files with the Quality Model's properties
	//public static final String PROPERTY_XML_PATH = new File(BenchmarkAnalyzer).getAbsolutePath();
	//public static final String PROPERTY_JSON_PATH = "C:/Users/Miltos/Desktop/properties.json";
	
	public static String BASE_DIR = new File(System.getProperty("user.dir")).getAbsolutePath();
	public static String BENCH_RESULT_PATH = new File(BASE_DIR + "/Results/Evaluation/BenchmarkResults").getAbsolutePath();

	
	/**
	 * This method exports the desired PropertySet into JSON format 
	 * to the desired path.
	 * 
	 * @param properties
	 * @param jsonPath
	 */
	public void exportPropertiesToJSON(PropertySet properties, String jsonPath){
		
		//Create a Gson json parser
		Gson gson = new Gson();
		
		//Parse your properties into a json String representation
		String json = gson.toJson(properties);
		
		try{
			FileWriter writer2 = new FileWriter(jsonPath);
			writer2.write(json);
			writer2.close();
		}catch(IOException e){
			System.out.println(e.getMessage());
		}
	}
}
