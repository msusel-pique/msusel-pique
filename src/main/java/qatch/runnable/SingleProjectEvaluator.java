package qatch.runnable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import qatch.analysis.*;
import qatch.evaluation.EvaluationResultsExporter;
import qatch.evaluation.Project;
import qatch.evaluation.ProjectCharacteristicsEvaluator;
import qatch.evaluation.ProjectEvaluator;
import qatch.model.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Vector;

/**
 * Behavioral class responsible for running TQI evaluation of a single project
 * in a language agnostic way.  It is the responsibility of extending projects
 * (e.g. qatch-csharp) to provide the language specific tools.
 */
public class SingleProjectEvaluator {

    private final Logger logger = LoggerFactory.getLogger(SingleProjectEvaluator.class);


    public Path runEvaluator(Path projectDir, Path resultsDir, Path qmLocation, ITool tool) {

        // initialize data structures
        initialize(projectDir, resultsDir, qmLocation);
        QualityModel qualityModel = makeNewQM(qmLocation);
        Project project = makeProject(projectDir);

        // run the static analysis tool process
        Map<String, Measure> measureResults = runTool(projectDir, tool);


        throw new NotImplementedException();
    }

    /**
     * Entry point for running single project evaluation. The library assumes the user has extended Qatch
     * by implementing IAnalyzer, IMetricsResultsImporter, IFindingsResultsImporter, IMetricsAggregator,
     * and IFindingsAggregator with language-specific functionality.
     *
     * This method then evaluates the measures, properties, characteristics, and TQI according to the provided
     * quality model.
     *
     * @param projectDir
     *      Path to root directory of project to be analyzed.
     * @param resultsDir
     *      Directory to place the analysis results in. Does not need to exist initially.
     * @param qmLocation
     *      Path to a completely derived quality model (likely .xml format).
     * @param metricsAnalyzer
     *      Analyzer provided by language-specific instance necessary to find metrics of the project.
     * @param findingsAnalyzer
     *      Analyzer provided by language-specific instance necessary to find findings of the project.
     * @param metricsImporter
     *      The language-specific importer that knows how to interpret metrics files produced by
     *      the metrics analyzer.
     * @param findingsImporter
     *      The language-specific importer that knows how to interpret findings files produced by
     *      the findings analyzer.
     * @param metricsAgg
     *      The language-specific object that describes how to aggregate metric values into property values.
     * @param findingsAgg
     *      The language-specific object that describes how to aggregate finding values into property values.
     * @return
     *      The path to the produced quality analysis file on the hard disk.
     */
//    public Path runEvaluator(Path projectDir, Path resultsDir, Path qmLocation,
//                             IAnalyzer metricsAnalyzer, IAnalyzer findingsAnalyzer,
//                             IMetricsResultsImporter metricsImporter, IFindingsResultsImporter findingsImporter,
//                             IMetricsAggregator metricsAgg, IFindingsAggregator findingsAgg) {
//
//        // initialize data structures
//        initialize(projectDir, resultsDir, qmLocation, metricsAnalyzer, findingsAnalyzer);
//        QualityModel qualityModel = makeNewQM(qmLocation);
//        Project project = makeProject(projectDir);
//
//        // run the static analysis tools
//        Path metricsResults = runMetricsTools(projectDir, resultsDir, qualityModel, metricsAnalyzer);
//        Path findingsResults = runFindingsTools(projectDir, resultsDir, qualityModel, findingsAnalyzer);
//        try {
//            project.setMetrics(getMetricsFromImporter(metricsResults, metricsImporter));
//            project.setIssues(getFindingsFromImporter(findingsResults, findingsImporter));
//        } catch (FileNotFoundException e) {
//            logger.error(e.getMessage());
//            throw new IllegalArgumentException(e.getMessage());
//        }
//
//        // aggregate static analysis values to their measure nodes
//        project.cloneProperties(qualityModel);
//        aggregateNormalize(project, metricsAgg, findingsAgg);
//
//        // evaluate higher QM nodes values with weights and values
//        evaluate(project, qualityModel);
//
//        // create a hard-disk file of the results and return to its path
//        return export(project, resultsDir);
//    }


    /**
     * Handles the Propertys issues and metrics by aggregating their Measure values according
     * to the quality model threshold calculation and finally normalizing.
     *
     * @param project
     *      The data structure representation of the project being evaluated.
     * @param metricsAgg
     *      The language-specific object that describes how to aggregate metric values into property values.
     * @param findingsAgg
     *      The language-specific object that describes how to aggregate finding values into property values.
     */
    void aggregateNormalize(Project project, IMetricsAggregator metricsAgg, IFindingsAggregator findingsAgg) {
        metricsAgg.aggregate(project);
        findingsAgg.aggregate(project);

        for(int i = 0; i < project.getProperties().size(); i++){
            Property property =  project.getProperties().get(i);
            property.getMeasure().calculateNormValue();
        }
    }


    /**
     * Calculate the values of each node at the properties, characteristics, then TQI layer.
     *
     * @param project
     *      The data structure representation of the project being evaluated.
     * @param qualityModel
     *      The data structure representation of the quality model being used for assessment.
     */
    void evaluate(Project project, QualityModel qualityModel) {
        ProjectEvaluator evaluator = new ProjectEvaluator();
        ProjectCharacteristicsEvaluator charEvaluator = new ProjectCharacteristicsEvaluator();

        // evaluate properties
        evaluator.evaluateProjectProperties(project);

        try {
            // evaluate characteristics
            for (int i = 0; i < qualityModel.getCharacteristics().size(); i++) {
                //Clone the characteristic and add it to the CharacteristicSet of the current project
                Characteristic c = (Characteristic) qualityModel.getCharacteristics().get(i).clone();
                project.getCharacteristics().addCharacteristic(c);
            }
            charEvaluator.evaluateProjectCharacteristics(project);

            // evaluate TQI
            project.setTqi((Tqi) qualityModel.getTqi().clone());
            project.calculateTQI();
        }
        catch (CloneNotSupportedException e) { e.printStackTrace(); }
    }


    /**
     * Export the evaluation results to JSON format on the hard drive
     *
     * @param project
     *      The data structure representation of the project being evaluated.
     * @param parentDir
     *      The directory to place the evaluation results file in.
     * @return
     *      The path pointing to the generated .json file.
     */
    Path export(Project project, Path parentDir) {
        String name = project.getName();
        File evalResults = new File(parentDir.toFile(), name + "_evalResults.json");
        EvaluationResultsExporter.exportProjectToJson(project, evalResults.toPath());
        return evalResults.toPath();
    }


    /**
     * Parse the findings files found in the provded directory to produce an object representation of the findings.
     *
     * @param findingsDirectory
     *      The directory all finding scan results from the analyzer tool are placed.
     * @param importer
     *      The language-specific importer that knows how to interpret the files in findingsDirectory.
     * @return
     *      A vector of IssueSet object representations of issues found
     * @throws FileNotFoundException
     *      Throws if none of the expected files can be found in the findings directory.
     */
    Vector<IssueSet> getFindingsFromImporter(Path findingsDirectory, IFindingsResultsImporter importer) throws FileNotFoundException {
        File[] results = findingsDirectory.toFile().listFiles();
        Vector<IssueSet> issues = new Vector<>();

        if (results == null) {
            throw new FileNotFoundException("Scanner results directory [" + findingsDirectory.toString() + "] has no files from static analysis.");
        }

        for (File resultFile : results) {
            try { issues.add(importer.parse(resultFile.toPath())); }
            catch (ParserConfigurationException | IOException | SAXException e) { e.printStackTrace(); }

        }

        return issues;
    }


    /**
     * Parse the metrics files found in the provded directory to produce an object representation of the metrics set.
     *
     * @param metricsDirectory
     *      The directory all metric scan results from the analyzer tool are placed.
     * @param importer
     *      The language-specific importer that knows how to interpret the files in metricsDirectory.
     * @return
     *      The metric set object representation of the project's metrics
     * @throws FileNotFoundException
     *      Throws if none of the expected files can be found in the metrics directory.
     */
    MetricSet getMetricsFromImporter(Path metricsDirectory, IMetricsResultsImporter importer) throws FileNotFoundException {
        File[] results = metricsDirectory.toFile().listFiles();
        if (results == null) {
            throw new FileNotFoundException("Scanner results directory [" + metricsDirectory.toString() + "] has no files from static analysis.");
        }

        for (File resultFile : results) {
            try { return importer.parse(resultFile.toPath()); }
            catch (IOException e) { e.printStackTrace(); }
        }

        throw new FileNotFoundException("Unable to find file to parse metrics from in directory " + metricsDirectory.toFile());
    }


    /**
     * Assert input parameters are valid and create the output folder
     *
     * @param projectDir
     *      Path to directory holding the project to be evaluated. Must exist.
     * @param resultsDir
     *      Directory to place the analysis results in. Does not need to exist initially.
     * @param qmLocation
     *      Path to the quality model file. Must exist.
     */
    void initialize(Path projectDir, Path resultsDir, Path qmLocation) {
        if (!projectDir.toFile().exists() || !projectDir.toFile().isDirectory()) {
            throw new IllegalArgumentException("Invalid projectDir path given.");
        }
        if (!qmLocation.toFile().exists() || !qmLocation.toFile().isFile()) {
            throw new IllegalArgumentException("Invalid qmLocation path given.");
        }

        resultsDir.toFile().mkdirs();
    }


    /**
     * Initialize basic QualityModel object with a name and empty characteristics and properties
     *
     * @param qmLocation
     *          Path to a completely derived quality model (likely .xml format).
     * @return
     *          Initialized quality model object
     */
    QualityModel makeNewQM(Path qmLocation) {
        QualityModelLoader qmImporter = new QualityModelLoader(qmLocation.toString());
        return qmImporter.importQualityModel();
    }


    /**
     * Initialize basic Project object
     *
     * @param projectDir
     *      Path to root directory of project to be analyzed
     * @return
     *      Basic object representation of the project
     */
    Project makeProject(Path projectDir) {
        Project project = new Project();
        project.setPath(projectDir.toAbsolutePath().toString());
        project.setName(projectDir.getFileName().toString());
        return project;
    }

    /**
     * Run static analysis tool evaluation process:
     *   (1) run static analysis tool
     *   (2) parse config: get object representation of the .yaml measure->diagnostics configuration
     *   (3) prase output: make collection of diagnostic objects
     *   (4) link findings and diagnostics to Measure objects
     *
     * A successful analysis results in the tool having a measureMappings instance variable
     * with similar structure to the input .yaml config but with Measure objects, and those Measure
     * objects have the actual findings from the analysis run included as Finding objects.
     *
     * @param projectDir
     *      Path to root directory of project to be analyzed.
     * @param tool
     *      Analyzer provided by language-specific instance necessary to find findings of the project.
     * @return
     *      A mapping of (Key: property name, Value: measure object) where the measure objects contain the
     *      static analysis findings for that measure.
     */
    private Map<String, Measure> runTool(Path projectDir, ITool tool) {

        // (1) run static analysis tool
        // TODO: turn this into a temp file that always deletes on/before program exit
        Path analysisOutput = tool.analyze(projectDir);

        // (2) parse config: get object representation of the .yaml measure->diagnostics configuration
        Map<String, Measure> propertyMeasureMap = tool.parseConfig(tool.getConfig());

        // (3) prase output: make collection of diagnostic objects
        Map<String, Diagnostic> analysisResults = tool.parseAnalysis(analysisOutput);

        // (4) link findings and diagnostics to Measure objects
        propertyMeasureMap = tool.applyFindings(propertyMeasureMap, analysisResults);

        return propertyMeasureMap;
    }

    /**
     * Creates a 'findings' folder in the resultsDir directory and places the results of
     * running the findingsAnalyzer tool in the finidngs folder.
     *
     * @param projectDir
     *      Path to root directory of project to be analyzed.
     * @param resultsDir
     *      Directory to place the analysis results in. Does not need to exist initially.
     *      A subdirectory 'findings' is placed in this directory.
     * @param qualityModel
     *      The quality model object representation of the quality model being used.
     * @param findingsAnalyzer
     *      Analyzer provided by language-specific instance necessary to find findings of the project.
     * @return
     *      The path to the directory the findingsAnalyzer static analysis results are placed.
     */
    Path runFindingsTools(Path projectDir, Path resultsDir, QualityModel qualityModel, IAnalyzer findingsAnalyzer) {
        File findingsResults = new File(resultsDir.toFile(), "findings");
        findingsResults.mkdirs();
        // TODO: add functionality for use of multiple analyzers
        findingsAnalyzer.analyze(projectDir, findingsResults.toPath(), qualityModel.getProperties());

        return findingsResults.toPath();
    }


    /**
     * Creates a 'metrics' folder in the resultsDir directory and places the results of
     * running the metricsAnalyzer tool in the metrics folder.
     *
     * @param projectDir
     *      Path to root directory of project to be analyzed.
     * @param resultsDir
     *      Directory to place the analysis results in. Does not need to exist initially.
     *      A subdirectory 'metrics' is placed in this directory.
     * @param qualityModel
     *      The quality model object representation of the quality model being used.
     * @param metricsAnalyzer
     *      Analyzer provided by language-specific instance necessary to find metrics of the project.
     * @return
     *      The path to the directory the metricsAnalyzer static analysis results are placed.
     */
    Path runMetricsTools(Path projectDir, Path resultsDir, QualityModel qualityModel, IAnalyzer metricsAnalyzer) {
        File metricsResults = new File(resultsDir.toFile(), "metrics");
        metricsResults.mkdirs();
        // TODO: add functionality for use of multiple analyzers
        metricsAnalyzer.analyze(projectDir, metricsResults.toPath(), qualityModel.getProperties());

        return metricsResults.toPath();
    }
}
