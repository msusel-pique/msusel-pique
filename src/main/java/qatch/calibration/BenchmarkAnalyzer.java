package qatch.calibration;

import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.text.Text;
import qatch.MoveToRunnableProject.CKJMAnalyzer;
import qatch.MoveToRunnableProject.PMDAnalyzer;
import qatch.analysis.IAnalyzer;
import qatch.model.PropertySet;
import qatch.utility.FileUtility;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

/**
 * This class is responsible for analyzing all the projects that are stored in the
 * desired folder (e.g. Benchmark Repository) against:
 * 		
 * 		1. all the supported tools of the system (e.g. PMD, CKJM etc.) and 
 * 		2. all the Properties of the Quality Model (e.g. Comprehensibility etc.)
 * 
 * The results are stored in a fixed results directory of known structure. 
 * This directory is automatically created by the BenchmarkAnalyzer based on
 * the structure of the benchmark directory.
 * 
 * Typically, it creates a different folder for each project found in the benchmark 
 * repository and places all the result files concerning this project in this folder.
 * 
 * @author Miltos
 *
 */
public class BenchmarkAnalyzer {
	
	// Useful static fields
	private final Path BASE_DIR = new File(System.getProperty("user.dir")).toPath();
	private Path RESULTS_PATH;
	private Path BENCH_RESULTS_PATH;
	private Path WORKSPACE_RESULTS_PATH;
	private Path SINGLE_PROJ_RESULTS_PATH;
	private Path BENCH_REPO_PATH;
	private PropertySet PROPERTY_SET;

	public BenchmarkAnalyzer(PropertySet properties, Path benchRepoPath, Path resultsPath){
		this.PROPERTY_SET = properties;
		this.BENCH_REPO_PATH = benchRepoPath;
		this.RESULTS_PATH = resultsPath;
		this.BENCH_RESULTS_PATH = Paths.get(this.RESULTS_PATH.toString(), "benchmark_results");
		this.WORKSPACE_RESULTS_PATH = Paths.get(this.RESULTS_PATH.toString(), "workspace_results");
		this.SINGLE_PROJ_RESULTS_PATH = Paths.get(this.RESULTS_PATH.toString(), "singleproject_results");

		this.RESULTS_PATH.toFile().mkdirs();
		this.BENCH_RESULTS_PATH.toFile().mkdirs();
		this.WORKSPACE_RESULTS_PATH.toFile().mkdirs();
		this.SINGLE_PROJ_RESULTS_PATH.toFile().mkdirs();
	}
	
	/**
	 * Setters and Getters.
	 */
	public Path getBASE_DIR() { return BASE_DIR; }
	public Path getRESULTS_PATH() { return RESULTS_PATH; }
	public Path getBENCH_RESULTS_PATH() { return BENCH_RESULTS_PATH; }
	public Path getWORKSPACE_RESULTS_PATH() { return WORKSPACE_RESULTS_PATH; }
	public Path getSINGLE_PROJ_RESULTS_PATH() { return SINGLE_PROJ_RESULTS_PATH; }
	public Path getBenchRepoPath() { return BENCH_REPO_PATH; }
	public PropertySet getProperties() { return PROPERTY_SET; }


	/**
	 * This method is responsible for analyzing the desired benchmark
	 * repository according to the user defined properties.
	 */
	public void analyzeBenchmarkRepo(IAnalyzer metricsAnalyzer, IAnalyzer findingsAnalyzer, String projectRootFlag) {

		System.out.println("\nBeginning repository benchmark analysis");

		Set<Path> projectRoots = FileUtility.multiProjectCollector(this.BENCH_REPO_PATH, projectRootFlag);
		projectRoots.forEach(p -> {
			metricsAnalyzer.analyze(metricsAnalyzer.targetSrcDirectory(p), BENCH_RESULTS_PATH, PROPERTY_SET);
			findingsAnalyzer.analyze(findingsAnalyzer.targetSrcDirectory(p), BENCH_RESULTS_PATH, PROPERTY_SET);
			System.out.println(p.getFileName().toString() + "analyzed.");
		});

		System.out.println("Repository benchmark analysis finished\n");
	}
	
	
	/**
	 * This method prints the structure of the benchmark directory to
	 * the console. 
	 * 
	 * It should be used for debugging purposes only.
	 */
	public void printBenchmarkRepoContents(){
		//List all the directories included inside the repository
		File baseDir = BENCH_REPO_PATH.toFile();
		System.out.println("Benchmark repository : " + baseDir.getAbsolutePath());
		File[] projects = baseDir.listFiles();
		for(int i = 0; i < projects.length; i++){
			if(projects[i].isDirectory()){
				System.out.println("Directory : " + projects[i].getName());
			}else{
				System.out.println("File : " + projects[i].getName());
			}
		}
		System.out.println("");
	}
}
