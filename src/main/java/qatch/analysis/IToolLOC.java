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
    Integer analyzeLinesOfCode(Path projectLocation);

    /**
     * All tool instances must define how to initialize and access their executable component given the tool's initial
     * root directory.  For example, if it is known the program will be running as a JAR and the tool is contained in
     * the JAR as a resource, this method would define how to extract the tool into a sibling folder and return the path
     * to the copied, now-accessable executable location.
     *
     * @param toolRoot
     *      The initial location of this tool's root folder.
     * @return
     *      Path to executable needed to run this tool.
     */
    Path initialize(Path toolRoot);
}
