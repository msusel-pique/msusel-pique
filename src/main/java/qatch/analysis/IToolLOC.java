package qatch.analysis;

import java.nio.file.Path;

/**
 * Interface definition for a single-purpose tool meant to get the lines of code in a project
 *
 * Lines of code is required for any project analysis so all Qatch-extending projects must
 * provide a way to get the value
 */
public interface IToolLOC {

    /**
     * Run the external analysis tool (often a binary or .exe)
     *
     * @param projectLocation
     *      Root directory location needed by the tool to perform its analysis on the given project
     * @return
     *      The number of lines of code of the project under analysis
     */
    int analyze(Path projectLocation);
}
