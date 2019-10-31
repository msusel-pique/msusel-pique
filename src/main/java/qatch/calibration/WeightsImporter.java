package qatch.calibration;

import com.google.gson.Gson;
import qatch.model.CharacteristicSet;
import qatch.model.Tqi;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;


/**
 * This class is responsible for importing the weights, 
 * that are calculated by R, into the desired Characteristic 
 * and Tqi objects of the quality model.
 *
 * The files containing the values of the weights are searched
 * inside R working directory.
 * 
 */
public class WeightsImporter {
	
	/**
	 * A method for importing the weights found in the "weights.json"
	 * file, which is exported by R analysis.
	 * 
	 * Precondition: This method should be executed only after the invocation
	 *               of R analysis and supposing that the json file is created.
	 *              
	 */
	@Deprecated
	public void importWeights(Tqi tqi, CharacteristicSet characteristics){
		
		try {
			//Create a BufferedReader in order to load the json file where the weights are stored
			BufferedReader br = new BufferedReader(new FileReader(RInvoker.R_WORK_DIR + "/weights.json"));
			
			//Create a Gson json Parser
			Gson gson = new Gson();
			
			//Parse the json into an  object of type Object
			Object obj = gson.fromJson(br,  Object.class);
			String s = obj.toString();
			gsonParser(s,tqi, characteristics);
			
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * This method implements a custom json parser that is used in order to load
	 * the weights of the TQI and the characteristics of the Quality Model and store
	 * them in the appropriate objects.
	 * 
	 * The order in which the weights are placed inside the json file doesn't matter
	 * because this method searches for the appropriate node. If the node isn't found
	 * inside the json file, a NullPointerException is thrown!!
	 * 
	 * TODO: Check the case where there a certain Characteristic cannot be found inside the predefined json file - Avoid NullPointerException
	 */
	@Deprecated
	public void gsonParser(String jsonLine, Tqi tqi, CharacteristicSet characteristics){
		throw new NotImplementedException();

//		//Get a JsonElement by using a jsonParser
//		JsonElement jelement = new JsonParser().parse(jsonLine);
//
//		//Typically, this json file is an object of arrays
//		JsonObject jobject = jelement.getAsJsonObject();
//
//		//Get the TQI weights
//		JsonArray tqiArray = jobject.get("tqi").getAsJsonArray();
//
//		for(int i = 0; i < tqiArray.size(); i++){
//			//Get the weights of the Tqi object
//			tqi.addWeight(tqiArray.get(i).getAsDouble());
//		}
//
//		//Search for each characteristic in the json file and get its weights
//		Characteristic characteristic = new Characteristic();
//		JsonArray charArray = new JsonArray();
//		for(int i = 0; i < characteristics.size(); i++){
//
//			//Get the current characteristic
//			characteristic = characteristics.get(i);
//
//			//Get the equivalent json array element
//			// R output replaces spaces in names with a '.' character.
//			String rCharName = characteristic.getName().replace(" ", ".");
//			charArray = jobject.get(rCharName).getAsJsonArray();
//
//			//Get the weights of the current characteristic
//			for(int j = 0; j < charArray.size(); j++){
//				characteristic.setWeight(characteristic.getName(), charArray.get(j).getAsDouble());
//			}
//		}
	}
}
