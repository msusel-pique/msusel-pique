package qatch.calibration;

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
@Deprecated
public class BenchmarkAnalyzer {
	
	// Useful static fields
	private final Path BASE_DIR = new File(System.getProperty("user.dir")).toPath();
	private Path RESULTS_PATH;
	private Path BENCH_RESULTS_PATH;
	private Path BENCH_REPO_PATH;
	private PropertySet PROPERTY_SET;

	public BenchmarkAnalyzer(PropertySet properties, Path benchRepoPath, Path resultsPath){
		this.PROPERTY_SET = properties;
		this.BENCH_REPO_PATH = benchRepoPath;
		this.RESULTS_PATH = resultsPath;
		this.BENCH_RESULTS_PATH = Paths.get(this.RESULTS_PATH.toString(), "benchmark_results");
		this.RESULTS_PATH.toFile().mkdirs();
		this.BENCH_RESULTS_PATH.toFile().mkdirs();
	}
	
	/**
	 * Setters and Getters.
	 */
	public Path getBASE_DIR() { return BASE_DIR; }
	public Path getRESULTS_PATH() { return RESULTS_PATH; }
	public Path getBENCH_RESULTS_PATH() { return BENCH_RESULTS_PATH; }
	public Path getBenchRepoPath() { return BENCH_REPO_PATH; }
	public PropertySet getProperties() { return PROPERTY_SET; }


	/**
	 * Run the tool-defined analyze() method and place the results in
	 * the folder defined by BENCH_RESULTS_PATH
	 *
	 * @param metricsAnalyzer
	 * 			Tool-class defined in a language-specific project that extends this framework.
	 * @param findingsAnalyzer
	 * 			Tool-class defined in a language-specific project that extends this framework.
	 * @param projectRootFlag
	 * 			Flag, usually a file extension, that signals that a project to be analyzed is
	 * 			within the directory the flag was found in.
	 */
	public void analyzeBenchmarkRepo(IAnalyzer metricsAnalyzer, IAnalyzer findingsAnalyzer, String projectRootFlag) {

		Set<Path> projectRoots = FileUtility.multiProjectCollector(this.BENCH_REPO_PATH, projectRootFlag);

		System.out.println("* Beginning repository benchmark analysis");
		System.out.println(projectRoots.size() + " projects to analyze.\n");

		projectRoots.forEach(p -> {
			File projFolder = new File(BENCH_RESULTS_PATH.toFile(), p.getFileName().toString());
			File findings = new File(projFolder, "findings");
			File metrics = new File(projFolder, "metrics");
			findings.mkdirs();
			metrics.mkdirs();

			metricsAnalyzer.analyze(p, metrics.toPath(), PROPERTY_SET);
			findingsAnalyzer.analyze(p, findings.toPath(), PROPERTY_SET);

			System.out.println(p.getFileName().toString() + " analyzed");
		});

		System.out.println("* Repository benchmark analysis finished");
	}
}
