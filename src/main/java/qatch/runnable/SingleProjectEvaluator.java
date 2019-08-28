package qatch.runnable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import qatch.analysis.IAnalyzer;
import qatch.analysis.IFindingsResultsImporter;
import qatch.analysis.IMetricsResultsImporter;
import qatch.evaluation.Project;
import qatch.model.IssueSet;
import qatch.model.MetricSet;
import qatch.model.QualityModel;
import qatch.model.QualityModelLoader;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Vector;

/**
 * Behavioral class responsible for running TQI evaluation of a single project
 * in a language agnostic way.  It is the responsibility of extending projects
 * (e.g. qatch-csharp) to provide the language specific tools.
 */
public class SingleProjectEvaluator {

    private final Logger logger = LoggerFactory.getLogger(SingleProjectEvaluator.class);


    public File evaluate(Path projectDir, Path resultsDir, Path qmLocation,
                         IAnalyzer metricsAnalyzer, IAnalyzer findingsAnalyzer,
                         IMetricsResultsImporter metricsImporter, IFindingsResultsImporter findingsImporter) {

        logger.info("* * * * * BEGINNING SINGLE PROJECT EVALUATION * * * * *");
        logger.info("Project to analyze: {}", projectDir.toString());

        initialize(projectDir, resultsDir, qmLocation, metricsAnalyzer, findingsAnalyzer);
        QualityModel qualityModel = makeNewQM(qmLocation);
        Project project = makeProject(projectDir);
        Path metricsResults = runMetricsTools(projectDir, resultsDir, qualityModel, metricsAnalyzer);
        Path findingsResults = runFindingsTools(projectDir, resultsDir, qualityModel, findingsAnalyzer);

        try {
            project.setMetrics(getMetricsFromImporter(metricsResults, metricsImporter, metricsAnalyzer.getResultFileName()));
            project.setIssues(getFindingsFromImporter(findingsResults, findingsImporter, findingsAnalyzer.getResultFileName()));
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }


        return null;
    }

    /**
     * Parse the findings files found in the provded directory to produce an object representation of the findings.
     *
     * @param findingsDirectory
     *      The directory all finding scan results from the analyzer tool are placed.
     * @param importer
     *      The language-specific importer that knows how to interpret the files in findingsDirectory.
     * @param fileNameMatch
     *      Regex pattern to ensure only expected metrics files are being considered. TODO: This can probably be removed.
     * @return
     *      A vector of IssueSet object representations of issues found
     * @throws FileNotFoundException
     *      Throws if none of the expected files can be found in the findings directory.
     */
    Vector<IssueSet> getFindingsFromImporter(Path findingsDirectory, IFindingsResultsImporter importer, String fileNameMatch) throws FileNotFoundException {
        File[] results = findingsDirectory.toFile().listFiles();
        Vector<IssueSet> issues = new Vector<>();

        if (results == null) {
            throw new FileNotFoundException("Scanner results directory [" + findingsDirectory.toString() + "] has no files from static analysis.");
        }

        for (File resultFile : results) {
            if (resultFile.getName().toLowerCase().contains(fileNameMatch.toLowerCase())) {
                try { issues.add(importer.parse(resultFile.toPath())); }
                catch (ParserConfigurationException | IOException | SAXException e) { e.printStackTrace(); }
            }
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
     * @param fileNameMatch
     *      Regex pattern to ensure only expected metrics files are being considered. TODO: This can probably be removed.
     * @return
     *      The metric set object representation of the project's metrics
     * @throws FileNotFoundException
     *      Throws if none of the expected files can be found in the metrics directory.
     */
    MetricSet getMetricsFromImporter(Path metricsDirectory, IMetricsResultsImporter importer, String fileNameMatch) throws FileNotFoundException {
        File[] results = metricsDirectory.toFile().listFiles();
        if (results == null) {
            throw new FileNotFoundException("Scanner results directory [" + metricsDirectory.toString() + "] has no files from static analysis.");
        }

        for (File resultFile : results) {
            if (resultFile.getName().toLowerCase().contains(fileNameMatch.toLowerCase())) {
                try { return importer.parse(resultFile.toPath()); }
                catch (IOException e) { e.printStackTrace(); }
            }
        }

        throw new FileNotFoundException("Unable to find file able to match with " + fileNameMatch + " in directory " + metricsDirectory.toFile());
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
     * @param metricsAnalyzer
     *      Analyzer provided by language-specific instance necessary to find metrics of the project
     * @param findingsAnalyzer
     *      Analyzer provided by language-specific instance necessary to find findings of the project
     */
    void initialize(Path projectDir, Path resultsDir, Path qmLocation, IAnalyzer metricsAnalyzer, IAnalyzer findingsAnalyzer) {
        if (!projectDir.toFile().exists() || !projectDir.toFile().isDirectory()) {
            throw new IllegalArgumentException("Invalid projectDir path given.");
        }
        if (!qmLocation.toFile().exists() || !qmLocation.toFile().isFile()) {
            throw new IllegalArgumentException("Invalid qmLocation path given.");
        }
        if (!metricsAnalyzer.getToolsDirectory().toFile().exists()) {
            throw new IllegalArgumentException("Invalid metrics tools directory given.");
        }
        if (!findingsAnalyzer.getToolsDirectory().toFile().exists()) {
            throw new IllegalArgumentException("Invalid findings tools directory given.");
        }

        resultsDir.toFile().mkdirs();
    }


    /**
     * Initialize basic QualityModel object with a name and empty characteristics and properties
     *
     * @param qmLocation
     *          Path to a completely derived quality model in XML format
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
