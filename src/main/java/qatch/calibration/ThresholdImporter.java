package qatch.calibration;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import qatch.model.PropertySet;

/**
 * This class is responsible for importing the thresholds, 
 * that are calculated by R, into the desired properties 
 * objects.
 * 
 * Typically, it contains different methods that allow to
 * import thresholds stored in different file formats.
 * 
 * The files containing the threshold values are searched
 * inside R working directory.
 * 
 */

@Deprecated
public class ThresholdImporter {

	/**
	 * A method for importing the thresholds from the "thresholds.json"
	 * file, which is exported by R analysis.
	 * 
	 * Precondition: This method should be executed only after the invocation
	 *               of R analysis and supposing that it created the json
	 *               file.
	 *              
	 */
	@Deprecated
	public void importThresholdsFromJSON(PropertySet properties){

//		try {
//			//Create a BufferedReader in order to load the json file where the thresholds are stored
//			BufferedReader br = new BufferedReader(new FileReader(RInvoker.R_WORK_DIR + "/threshold.json"));
//
//			//Create a Gson json Parser
//			Gson gson = new Gson();
//
//			//Parse the json into an  object of type Object
//			Object obj = gson.fromJson(br,  Object.class);
//			String s = obj.toString();
//			gsonParser(s,properties);
//
//		} catch (FileNotFoundException e) {
//			System.out.println(e.getMessage());
//		}
	}
	
	/**
	 * A custom parser for parsing the thresholds and storing them in 
	 * the appropriate Property objects of the desired property set.
	 * 
	 * @param jsonLine   : The json string that should be parsed in 
	 *                     order to extract the thresholds.
	 * @param properties : The PropertySet object with the Property
	 *                     objects where the extracted thresholds 
	 *                     should be placed.
	 *                     
	 * ATTENTION:
	 *   - This method makes the assumption that the thresholds are
	 *     placed in the correct order. This means that the thresholds
	 *     are in an ascending order and the json object sequence 
	 *     corresponds to the sequence of the properties stored in the
	 *     desired PropertySet.
	 */
	/*
	 * Exactly as we do at BenchmarkResultsImporter where we load an IssueSet and
	 * we search to find the Issue with the same name in the desirable IssueSet
	 * 
	 */
	@Deprecated
	public void gsonParser(String jsonLine, PropertySet properties){
		
		//Get a JsonElement by using a jsonParser
		JsonElement jelement = new JsonParser().parse(jsonLine);
		
		//Typically, this json file is an array of objects
		//Get the array of objects
		JsonArray jarray = jelement.getAsJsonArray();
		
		//For each object of the json array do...
		for(int i = 0; i < jarray.size(); i++){
			//Get the current object
			JsonObject jobject = jarray.get(i).getAsJsonObject();
			//Get the thresholds of the i-th object
			Double[] t = new Double[3];
			t[0] = Double.parseDouble(jobject.get("t1").toString());
			t[1] = Double.parseDouble(jobject.get("t2").toString());
			t[2] = Double.parseDouble(jobject.get("t3").toString());

			/*
			 * Store the thresholds in the threshold array of the i-th property.
			 * No check is made - Assumption: Everything is in the correct order.
			 */

			properties.get(i).setThresholds(t);
		}
	}
}
