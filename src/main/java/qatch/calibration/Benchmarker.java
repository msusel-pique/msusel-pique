package qatch.calibration;


import qatch.analysis.ITool;
import qatch.analysis.IToolLOC;
import qatch.evaluation.Project;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

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
    private final Path qmDescription;  // location of quality model description file
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
    public Benchmarker(Path benchmarkRepository, Path qmDescription) {
        this.benchmarkRepository = benchmarkRepository;
        this.qmDescription = qmDescription;
    }


    // Methods
    public Path analyze(Path repository) {
        throw new NotImplementedException();
    }

    public Map<String, Double[]> generateThresholds(Path findings) {
        throw new NotImplementedException();
    }
}
