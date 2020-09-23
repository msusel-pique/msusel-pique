package pique.calibration;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.opencsv.CSVWriter;
import pique.model.Diagnostic;
import pique.analysis.ITool;
import pique.model.Measure;
import pique.evaluation.Project;
import pique.model.QualityModel;
import pique.utility.FileUtility;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * This class is responsible for analyzing all the projects that are stored in a
 * benchmark repository against
 * 		1. all the supported analysis tools of the system (ITool instances) and
 * 		2. all the Properties of the Quality Model.
 * to produce thresholds associated to each property of the quality model.
 *
 * The Benchmarker provides functionality to run batch tool analysis and run R threshold
 * generation on the resulting ProductFactor-Finding pairings from each benchmark project.
 */
public class DefaultBenchmarker implements IBenchmarker {


    // Methods
    @Override
    public Map<String, Double[]> deriveThresholds(Path benchmarkRepository, QualityModel qmDescription, Set<ITool> tools, String projectRootFlag, Path analysisResults, Path rThresholdsOutput) {

        // Collect root paths of each benchmark project
        Set<Path> projectRoots = FileUtility.multiProjectCollector(benchmarkRepository, projectRootFlag);
        ArrayList<Project> projects = new ArrayList<>();

        System.out.println("* Beginning repository benchmark analysis");
        System.out.println(projectRoots.size() + " projects to analyze.\n");

        int totalProjects = projectRoots.size();
        int counter = 0;

        for (Path projectPath : projectRoots) {

            counter++;

            // Instantiate new project object
            Project project = new Project(projectPath.getFileName().toString(), projectPath, qmDescription);

            // Run the static analysis tools process
            Map<String, Diagnostic> allDiagnostics = new HashMap<>();
            tools.forEach(tool -> {
                Path analysisOutput = tool.analyze(projectPath);
                allDiagnostics.putAll(tool.parseAnalysis(analysisOutput));
            });

            // Run LOC tool to set lines of code
            project.setLinesOfCode((int) allDiagnostics.get("loc").getValue());

            // Apply collected diagnostics (containing findings) to the project
            allDiagnostics.forEach((diagnosticName, diagnostic) -> {
                project.addFindings(diagnostic);
            });

            // Evaluate project up to Measure level (normalize does happen first)
            project.evaluateMeasures();

            // Add new project (with tool findings information included) to the list
            projects.add(project);

            // Print information
            System.out.println("\n\tFinished analyzing project " + project.getName());
            System.out.println("\t" + counter + " of " + totalProjects + " analyzed.\n");
        }

        // Create [Project_Name:Measure_Values] matrix file
        createProjectMeasureMatrix(projects, analysisResults);

        // Run the R script to generate thresholds
        return rThresholdRunnerMapper(rThresholdsOutput, analysisResults);
    }


    @Override
    public double utilityFunction(double input, Double[] thresholds, boolean positive) {
        if (thresholds.length > 3) throw new RuntimeException("DefalutBenchmarker.utilityFunction expectes the measure's thresholds[].size == 3");

        Double lowerBound = thresholds[0];
        Double middleBound = thresholds[1];
        Double upperBound = thresholds[2];

        double middleGroupEval = (0.5 / (middleBound - lowerBound)) * (input - lowerBound);
        double upperGroupEval = (0.5 / (upperBound - middleBound)) * (upperBound - input);

        if (positive) {
            //If the metric has a positive impact on quality -> Ascending utility function
            if (input <= lowerBound) {
                //Lower Group
                return(0.0);
            } else if (input <= middleBound) {
                //Middle Group
                return(middleGroupEval);
            } else if (input <= thresholds[2]) {
                //Upper Group
                return( 1 - upperGroupEval);
            } else {
                //Saturation
                return(1.0);
            }
        } else {
            //If the metric has a negative impact on quality -> Descending utility function
            if (input <= lowerBound) {
                //Lower Group
                return(1.0);
            } else if (input <= middleBound) {
                //Middle Group
                return(1.0 - middleGroupEval);
            } else if (input <= thresholds[2]) {
                //Upper Group
                return(upperGroupEval);
            } else {
                //Saturation
                return(0.0);
            }
        }
    }

    @Override
    public String getName() {
        return "pique.calibration.DefaultBenchmarker";
    }


    /**
     * Execute the R script for theshold derivation to create thesholds for each property in the
     * quality model description according to the benchmark repository analysis findings.
     *
     * This method assumes the analysisResults fields points to an existing, generated file path
     * as a precondition.
     *
     * @param output
     *      Path to desired directory to hold the output of running the R Thresholds script.
     *      The disk file may be considered temporary (only needed for the scope of this method)
     *      and can be deleted after method execution.
     * @param analysisResults
     *      Input file for R Threshold script: matrix of project name's and their measure values
     * @return
     *      A mapping of property's measure names to the associated thresholds of that measure's property
     */
    public static Map<String, Double[]> rThresholdRunnerMapper(Path output, Path analysisResults) {

        // Precondition check
        if (!analysisResults.toFile().isFile()) {
            throw new RuntimeException("Benchmark analysisResults field must point to an existing file");
        }

        // Prepare temp file for R Script results
        output.toFile().mkdirs();
        File thresholdsFile = new File(output.toFile(), "threshold.json");

        // R script expects the directory containining the analysis results as a parameter
        Path analysisDirectory = analysisResults.getParent();

        // Run R Script
        RInvoker.executeRScript(RInvoker.Script.THRESHOLD, analysisDirectory, output, FileUtility.getRoot());

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

        return thresholds;
    }


    // Helper methods

    /**
     * Transform a collection of Project objects into a matrix (likely .csv) with the Project name
     * as the rows and the normalized measure values of that project as the columns.
     *
     * @param projects
     *      Collection of projects.
     * @param analysisResults
     *      The file to write the matrix to.
     * @return
     *      The path to the matrix file on the hard drive.
     */
    public static Path createProjectMeasureMatrix(List<Project> projects, Path analysisResults) {

        // Ensure containing directory of matrix output file exists
        analysisResults.getParent().toFile().mkdirs();
        // Make ArrayList of measures to assert order of measure data retrieval
        ArrayList<Measure> measureList = new ArrayList<>(projects.get(0).getQualityModel().getMeasures().values());
        // Basic matrix data
        int numMeasures = measureList.size();

        try {
            FileWriter fw = new FileWriter(analysisResults.toFile());
            CSVWriter writer = new CSVWriter(fw);

            // Build rows of string arrays to eventually feed to writer
            ArrayList<String[]> csvRows = new ArrayList<>();

            // Header
            String[] header = new String[numMeasures + 1];
            header[0] = "Project_Name";
            for (int i = 1; i < measureList.size() + 1; i++) {
                header[i] = measureList.get(i-1).getName();
            }
            csvRows.add(header);

            // Additional rows
            for (Project project : projects) {
                String[] row = new String[numMeasures + 1];
                row[0] = project.getName();
                for (int i = 1; i < measureList.size() + 1; i++) {
                    row[i] = String.valueOf(project.getQualityModel().getMeasure(measureList.get(i-1).getName()).getValue((double)project.getLinesOfCode()));
                }
                csvRows.add(row);
            }

            // Run writer
            csvRows.forEach(writer::writeNext);
            writer.close();
            fw.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return analysisResults;
    }

}
