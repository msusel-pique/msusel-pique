package qatch.calibration;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.nio.file.Path;

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
@Deprecated
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
	@Deprecated
	public Path exportToCsv(BenchmarkProjects projects) {

//		//Set the path where the csv file will be stored and the name of the csv file
//		File outFile = new File(RInvoker.R_WORK_DIR.toFile(), "properties.csv");
//
//		//Create the folder
//		RInvoker.R_WORK_DIR.toFile().mkdirs();
//
//		return outFile.toPath();
		throw new NotImplementedException();
	}
}
