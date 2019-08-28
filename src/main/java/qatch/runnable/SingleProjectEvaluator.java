package qatch.runnable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;

/**
 * Behavioral class responsible for running TQI evaluation of a single project
 * in a language agnostic way.  It is the responsibility of extending projects
 * (e.g. qatch-csharp) to provide the language specific tools.
 */
public class SingleProjectEvaluator {

    private final Logger logger = LoggerFactory.getLogger(SingleProjectEvaluator.class);


    public File evaluate(Path projectDir, Path resultsDir, Path qmLocation, Path toolsRoot) {

        logger.info("****************** BEGINNING SINGLE PROJECT EVALUATION ******************");
        logger.info("Project to analyze: {}", projectDir.toString());
        logger.info("Results location: {}\n", resultsDir.toString());

        resultsDir.toFile().mkdirs();

        return null;
    }

    void initialize(Path projectDir, Path resultsDir, Path qmLocation, Path toolsRoot) {
        try {
            if (!projectDir.toFile().exists() || !projectDir.toFile().isDirectory()) {
                throw new IllegalArgumentException("Invalid projectDir path given.");
            }
            if (!qmLocation.toFile().exists() || !qmLocation.toFile().isFile()) {
                throw new IllegalArgumentException("Invalid qmLocation path given.");
            }
            if (!toolsRoot.toFile().exists() || !toolsRoot.toFile().isDirectory()) {
                throw new IllegalArgumentException("Invalid toolsRoot path given.");
            }
        } finally {
            resultsDir.toFile().mkdirs();
        }
    }
}
