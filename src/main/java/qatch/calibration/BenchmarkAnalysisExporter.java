package qatch.calibration;

import com.google.gson.Gson;
import com.opencsv.CSVWriter;
import qatch.evaluation.Project;
import qatch.model.Property;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * This class is responsible for exporting the results of the benchmark analysis
 * and aggregation in different file formats, so that they can be used by R in
 * order to calculate the thresholds of each property.
 * 
 * More specifically, it does the following:
 * 
 * 	1. Receives a set of projects (i.e. BenchmarkProjects object) as an input.
 * 	   (The projects of the project set should have their normValue field for 
 * 	   each Property object of their PropertySet calculated)
 *  2. It creates the appropriate file (e.g. csv, json etc.) inside the working
 *     directory of R.
 *  3. It iterates through all the properties of each project of the project set
 *     and writes their normValue values inside the appropriate cells of the file.
 *     
 * ATTENTION:
 *   - The properties should be exported in the appropriate order. The order of the
 *     columns of the xls file (or the json objects of the json file) should agree 
 *     with the order of the Property objects found in the PropertySet of each project,
 *     or the total PropertySet containing the Properties of the model in general!!!
 *   
 * @author Miltos
 *
 */

public class BenchmarkAnalysisExporter {

	/**
	 * A method for exporting the normValue fields of each Project's Property objects
	 * into a csv file. The csv file is placed inside R's working directory in order
	 * to be processed by the appropriate R script. (see RInvoker)
	 * 
	 * The csv file exported by this method has the following form:
	 *    - Each row corresponds to a single project of the BenchmarkProjects object.
	 *    - Each column corresponds to a single Property of the model's properties
	 *    - Each cell contains the calculated normalized value (normValue) of each 
	 *    	project's property.
	 *    
	 *  ATTENTION:
	 *    - The order of the columns is equivalent to the order of the Properties of
	 *      the total PropertySet containing the model's properties.
	 */
	public Path exportToCsv(BenchmarkProjects projects){
		
		//Set the path where the csv file will be stored and the name of the csv file
		File outFile = new File(RInvoker.R_WORK_DIR.toFile(), "properties.csv");
		
		//Create the folder
		RInvoker.R_WORK_DIR.toFile().mkdirs();

		// create the csv file
		try {
			FileWriter fw = new FileWriter(outFile);
			CSVWriter writer = new CSVWriter(fw);
			int numCols = projects.getProject(0).getProperties().size() + 1;
			// build rows of string arrays to eventually feed to writer
			ArrayList<String[]> csvRows = new ArrayList<>();

			// header
			String[] header = new String[numCols];
			header[0] = "Project_Name";
			for(int i = 0; i < projects.getProject(0).getProperties().size(); i++){
				//Get the i-th property
				Property p = projects.getProject(0).getProperties().get(i);
				//Set the name of the i-th column to the name of this Property
				header[i+1] = p.getName();
			}
			csvRows.add(header);

			// body: each project's normalized propertys' values
			projects.getProjects().forEach(p -> {
				String[] currentRow = new String[numCols];
				currentRow[0] = p.getName();
				// TODO: another situation of proper order of properties being needed. Eventually refactor to hash lookup
				for (int i = 0; i < p.getProperties().size(); i++) {
					Property property = p.getProperties().get(i);
					currentRow[i+1] = String.valueOf(property.getMeasure().getNormValue());
				}
				csvRows.add(currentRow);
			});

			csvRows.forEach(writer::writeNext);
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return outFile.toPath();
	}
	
	/**
	 * A method for exporting only the properties of the projects of a set of
	 * projects in a json format. Typically, it exports only the PropertySet
	 * object of each Project found in a property set (object of class BenchmarkProjects).
	 * 
	 * USAGES:
	 *   - It can be used for debugging purposes. Use it instead of exporting 
	 *     everything found in a BenchmarkProject object. (more lightweight)
	 *   - As an example (tutorial), on how to create your own json file.
	 */

	public void exportToJSON(BenchmarkProjects projects, String path){

		//Create the json parser
		Gson gson = new Gson();
		
		//Create the string containing the total json file
		String totalJson = "{\"projects\":[";
		
		//Iterate through the projects
		for(int i = 0; i < projects.size(); i++){
			
			Project project = projects.getProject(i);
			String json = gson.toJson(project.getProperties());
			totalJson += json + ",";
		}
		
		//Close the json file appropriately 
		totalJson += "]}";

		//Save the json file inside R Working Directory for Manipulation
		try{
			FileWriter writer = new FileWriter(path);
			writer.write(totalJson);
			writer.close();
		}catch(IOException e){
			System.out.println(e.getMessage());
		}
	}
}
