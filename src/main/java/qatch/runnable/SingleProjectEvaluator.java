package qatch.runnable;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qatch.analysis.Diagnostic;
import qatch.analysis.ITool;
import qatch.analysis.IToolLOC;
import qatch.evaluation.Project;
import qatch.model.QualityModel;

import java.nio.file.Path;
import java.util.Map;

/**
 * Behavioral class responsible for running TQI evaluation of a single project
 * in a language agnostic way.  It is the responsibility of extending projects
 * (e.g. qatch-csharp) to provide the language specific tools.
 */
// TODO: turn into static methods (maybe unelss logger problems)
public class SingleProjectEvaluator {

    private final Logger logger = LoggerFactory.getLogger(SingleProjectEvaluator.class);


    /**
     * Entry point for running single project evaluation. The library assumes the user has extended Qatch
     * by implementing ITool and IToolLOC with language-specific functionality.
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
     * @param tool
     *      A language-specific tool implementation to collect diagnostics and findings
     * @param locTool
     *      A language-specific tool whose only job is to get the lines of code of the project under analysis
     * @return
     *      The path to the produced quality analysis file on the hard disk.
     */
    public Path runEvaluator(Path projectDir, Path resultsDir, Path qmLocation, ITool tool, IToolLOC locTool) {

        // initialize data structures
        initialize(projectDir, resultsDir, qmLocation);
        QualityModel qualityModel = new QualityModel(qmLocation);
        Project project = new Project(FilenameUtils.getBaseName(projectDir.getFileName().toString()), projectDir, qualityModel);

        // run the static analysis tools process
        Map<String, Diagnostic> diagnosticResults = runTool(projectDir, tool);
        int projectLoc = locTool.analyze(projectDir);

        // apply tool results to Project object
        project.setDiagnostics(diagnosticResults);
        project.setLinesOfCode(projectLoc);

        // evaluate measure nodes (normalize using lines of code)
        project.evaluateMeasures();

        // aggregate properties -> characteristics -> tqi values using quality model (thresholds for properties and weights for characteristics and tqi)
        project.evaluateProperties();
        project.evaluateCharacteristics();
        project.evaluateTqi();

        // create a file of the results and return its path
        return project.exportToJson(resultsDir);
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
        if (!projectDir.toFile().exists()) {
            throw new IllegalArgumentException("Invalid projectDir path given.");
        }
        if (!qmLocation.toFile().exists() || !qmLocation.toFile().isFile()) {
            throw new IllegalArgumentException("Invalid qmLocation path given.");
        }

        resultsDir.toFile().mkdirs();
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
    private Map<String, Diagnostic> runTool(Path projectDir, ITool tool) {

        // (1) run static analysis tool
        // TODO: turn this into a temp file that always deletes on/before program exit
        Path analysisOutput = tool.analyze(projectDir);

        // (2) prase output: make collection of {Key: diagnostic name, Value: diagnostic objects}
        return tool.parseAnalysis(analysisOutput);
    }
}
