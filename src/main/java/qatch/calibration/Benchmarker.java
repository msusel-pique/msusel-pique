package qatch.calibration;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;
import qatch.analysis.ITool;
import qatch.analysis.IToolLOC;
import qatch.evaluation.Project;
import qatch.model.QualityModel;
import qatch.utility.FileUtility;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class is responsible for analyzing all the projects that are stored in a
 * benchmark repository against
 * 		1. all the supported analysis tools of the system (ITool instances) and
 * 		2. all the Properties of the Quality Model.
 * to produce thresholds associated to each property of the quality model.
 *
 * The Benchmarker provides functionality to run batch tool analysis and run R threshold
 * generation on the resulting Property-Finding pairings from each benchmark project.
 */
public class Benchmarker {

    // Fields
    private Path analysisResults;  // location of hard-disk file of analysis results (disk file necessary for R script)
    private final Path benchmarkRepository;  // location of root foldering containing language-specific projects for benchmarking
    private QualityModel qmDescription;  // location of quality model description file
    private IToolLOC locTool;  // loc-specific purpose tool, necessary for normalization
    private Map<String, Project> projects = new HashMap<>();  // { key: project name, value: project object }
    private Map<String, ITool> tools = new HashMap<>();  // tools intended for static analysis of diagnostic findings
    private Map<String, Double[]> thresholds = new HashMap<>();  // { key: property name, value: property thresholds }


    // Constructor
    /**
     * @param benchmarkRepository
     *      Location of root foldering containing language-specific projects for benchmarking
     * @param qmDescription
     *      Location of quality model description file
     */
    Benchmarker(Path benchmarkRepository, Path qmDescription) {
        this.benchmarkRepository = benchmarkRepository;
//        this.qmDescription = qmDescription;
    }


    // Getters and setters
    public Path getAnalysisResults() {
        return analysisResults;
    }
    void setAnalysisResults(Path analysisResults) {
        this.analysisResults = analysisResults;
    }


    // Methods
    /**
     * Run tools on all benchmark projects to collect normalized values of each property across each project
     * Knowledge of properties to associate with tool findings comes from the quality model description.
     *
     * @param projectRootFlag
     *      Flag, usually a file extension, that signals that a project to be analyzed is
     *      within the directory the flag was found in.
     * @return
     *      The path to the file containing normalized values of each property across each project
     */
    public Path analyze(String projectRootFlag) {

        // Collect root paths of each benchmark project
        Set<Path> projectRoots = FileUtility.multiProjectCollector(this.benchmarkRepository, projectRootFlag);

        System.out.println("* Beginning repository benchmark analysis");
        System.out.println(projectRoots.size() + " projects to analyze.\n");


        for (Path projectPath : projectRoots) {

            // Instantiate new project object
//            Project project = new Project(projectPath.getFileName().toString(), projectPath, );

            // Run tool to set lines of code
//            project.setLinesOfCode(locTool.analyze(projectPath));

            // Run tool to find occurances of each diagnostic for each property
        }

        throw new NotImplementedException();
    }

    /**
     * Execute the R script for theshold derivation to create thesholds for each property in the
     * quality model description according to the benchmark repository analysis findings.
     *
     * This method assumes the analysisResults fields points to an existing, generated file path
     * as a precondition.
     *
     * @return
     *      A mapping of property names to the associated thresholds of that property
     */
    Map<String, Double[]> generateThresholds(Path output) {

        // Precondition check
        if (!this.analysisResults.toFile().isFile()) {
            throw new RuntimeException("Benchmark analysisResults field must point to an existing file");
        }

        // Prepare temp file for R Script results
        output.toFile().mkdirs();
        File thresholdsFile = new File(output.toFile(), "threshold.json");

        // R script expects the directory containining the analysis results as a parameter
        Path analysisDirectory = this.analysisResults.getParent();

        // Run R Script
        RInvoker.executeRScript(RInvoker.Script.THRESHOLD, analysisDirectory.toString(), output.toString());
        if (!thresholdsFile.isFile()) {
            throw new RuntimeException("Execution of R script did not result in an existing file at " + thresholdsFile.toString());
        }

        // Build object representation of data from R script
        Map<String, Double[]> thresholds = new HashMap<>();
        try {
            FileReader fr = new FileReader(thresholdsFile.toString());
            JsonArray jsonEntries = new JsonParser().parse(fr).getAsJsonArray();

            for (JsonElement entry : jsonEntries) {
                JsonObject jsonProperty = entry.getAsJsonObject();
                String name = jsonProperty.getAsJsonPrimitive("_row").getAsString().replaceAll("\\.", " ");
                Double[] threshold = new Double[] {
                        jsonProperty.getAsJsonPrimitive("t1").getAsDouble(),
                        jsonProperty.getAsJsonPrimitive("t2").getAsDouble(),
                        jsonProperty.getAsJsonPrimitive("t3").getAsDouble()
                };
                thresholds.put(name, threshold);
            }

            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Delete temporary artifacts
        try { FileUtils.deleteDirectory(output.toFile()); }
        catch (IOException e) { e.printStackTrace(); }

        this.thresholds = thresholds;
        return thresholds;
    }
}
