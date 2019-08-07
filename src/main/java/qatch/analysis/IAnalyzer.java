package qatch.analysis;

import qatch.model.PropertySet;

import java.nio.file.Path;

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
	 * @param src : The path representation to the project to be analyzed
	 * @param dest : The path representation to the destination folder where the results will be placed.
	 * @param properties : The set of properties against which the project will be analyzed.
	 *
	 * Typically this method does the following:
	 *
	 * 		1. Iterates through the PropertySet
	 * 		2. For each Property object the method calls the analyze() method in order to
	 * 	       analyze the project against this single property.
	 */
	// TODO: return an AnalysisResults data object instead of relying on the tool's generated hard drive file
	void analyze(Path src, Path dest, PropertySet properties);

	/**
	 * Very hacky way of dealing with 2 related analysis tools that need their src patch to be in
	 * different directories. This should be refactored when time
	 *
	 * @param src
	 * 		The original directory intended to run analysis on
	 * @return
	 * 		The modified directory (relative to src) to point to
	 */
	Path targetSrcDirectory(Path src);
}
