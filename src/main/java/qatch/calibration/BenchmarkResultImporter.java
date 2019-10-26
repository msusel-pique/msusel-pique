package qatch.calibration;

import org.xml.sax.SAXException;
import qatch.analysis.IFindingsResultsImporter;
import qatch.analysis.IMetricsResultsImporter;
import qatch.evaluation.Project;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** 
 * This class is responsible for importing all the results that the
 * BenchmarkAnalyzer exported in the fixed results directory.
 * 
 * Typically it does the following:
 * 	1. It searches the results directory for project results
 * 	2. For each folder it creates a Project object 
 *  3. It calls the metrics and findings importers in order to import the results of each project
 *     and stores them in the corresponding fields of the Project object
 *  4. It returns an object of type BenchmarkProjects. This object is simply a Vector of Project
 *     objects containing the imported project results.
 */

@Deprecated
public class BenchmarkResultImporter {
	
	/**
	 * The method that implements the whole functionality of this class.
	 */
	public BenchmarkProjects importResults(BenchmarkAnalyzer analyzer,
										   IMetricsResultsImporter metricsImp,
										   IFindingsResultsImporter findingsImp) {

		//Create an empty BenchmarkProject object
		BenchmarkProjects projects = new BenchmarkProjects();

		//Import the results
		// TODO: refactor this disturbing hellscape into private method calls
		try {
			Files.list(analyzer.getBENCH_RESULTS_PATH())
					// for each analyzed project...
					.forEach(p -> {
						Project project = new Project(p.getFileName().toString());
						project.setPath(p.toAbsolutePath().toString());
						// parse and set metrics and issues found by the tools
						Path metricsFolder = Paths.get(p.toString(), "metrics");
						Path findingsFolder = Paths.get(p.toString(), "findings");

						try {
							Files.list(metricsFolder)
									.filter(Files::isRegularFile)
									.forEach(f -> {
										try { project.setMetrics(metricsImp.parse(f)); }
										catch (IOException e) {	e.printStackTrace(); }
									});
						} catch (IOException e) {e.printStackTrace(); }

//						try {
//							Files.list(findingsFolder)
//									.filter(Files::isRegularFile)
//									.forEach(f -> {
//										try { project.addIssueSet(findingsImp.parse(f)); }
//										catch (IOException | ParserConfigurationException | SAXException e) {
//											e.printStackTrace();
//										}
//									});
//						} catch (IOException e) { e.printStackTrace(); }

						projects.addProject(project);
					});
		} catch (IOException e) { e.printStackTrace(); }

		return projects;
	}
}
