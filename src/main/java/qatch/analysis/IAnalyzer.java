package qatch.analysis;

import qatch.model.PropertySet;

import java.io.File;

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
	 * @param src : The File path representation to the project to be analyzed
	 * @param dest : The File path representation to the destination folder where the results will be placed.
	 * @param properties : The set of properties against which the project will be analyzed.
	 *
	 * Typically this method does the following:
	 *
	 * 		1. Iterates through the PropertySet
	 * 		2. For each Property object the method calls the analyze() method in order to
	 * 	       analyze the project against this single property.
	 */
	void analyze(File src, File dest, PropertySet properties);
}
