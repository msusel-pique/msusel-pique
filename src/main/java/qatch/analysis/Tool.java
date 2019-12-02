package qatch.analysis;

import qatch.utility.FileUtility;

import java.nio.file.Path;

public abstract class Tool implements ITool {

    // Instance vars
    private String name;
    private Path location;


    // Constructor
    /**
     * On construction of any tool, Qatch will copy the tool files inside of the language-specific Qatch run into
     * a temporary folder named 'tools'.  This handles problems with accessing executables inside of JAR files.
     *
     * @param name
     *      The tool name
     * @param initialToolLocation
     *      The initial location of this tool's root folder
     * @param tempTargetDirectory
     *      The location to place the 'tools' directory which this tools runnable contents will be extracted in to.
     */
    public Tool(String name, Path initialToolLocation, Path tempTargetDirectory) {
        this.name = name;
        this.location = extractTool(initialToolLocation, tempTargetDirectory);
    }


    // Getters and setters
    public String getName() { return name; }

    /**
     * @return
     *      Root directory for this tool. For example, if the executable for tool MyTool is located at
     *      /home/my_project/tools/MyTool/bin/MyTool.exe, this method should return path
     *      /home/my_project/tools/MyTool.
     */
    public Path getLocation() {
        return location;
    }


    // Methods

    /**
     * Create temporary copy of this tool into a directory named after the tool.
     */
    private Path extractTool(Path initialToolLocation, Path destination) {

        // Extract tool as temporary resource
        return FileUtility.extractResources(destination, initialToolLocation);

    }
}
