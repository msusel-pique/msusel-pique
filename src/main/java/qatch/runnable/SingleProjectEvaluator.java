package qatch.runnable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Behavioral class responsible for running TQI evaluatin of a single project
 * in a language agnostic way.  It is the responsibility of extending projects
 * (e.g. qatch-csharp) to provide the language specific tools.
 */
public class SingleProjectEvaluator {

    private final static Logger logger = LoggerFactory.getLogger(SingleProjectEvaluator.class);

    public static void main(String[] args) {
        int i = 14;
        logger.debug("i is set to {}", i);
        logger.info("this is an info message.");
        logger.error("this is an error message.");
    }

}
