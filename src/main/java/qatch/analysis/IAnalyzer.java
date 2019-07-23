package qatch.analysis;

import qatch.model.PropertySet;

/**
 * This is an interface class that describes the minimum
 * functionality that an Analyzer should have in order
 * to work properly.
 *
 * e.g. PMDAnalyzer, CKJMAnalyzer etc.
 */

public interface IAnalyzer {
	
	/**
	 * This method is responsible for the execution of the desired static analysis
	 * on the desired project.
	 * @param src : The path to the project to be analyzed
	 * @param dest : The path to the destination folder where the results will be placed.
	 * 
	 */
	void analyze(String src, String dest, PropertySet properties);
}
