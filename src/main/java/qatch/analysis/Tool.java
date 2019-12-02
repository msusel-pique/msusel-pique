package qatch.analysis;

import java.nio.file.Path;

public abstract class Tool implements ITool {

    // Instance vars
    private String name;
    private Path executable;
    private Path toolRoot;


    // Constructor
    /**
     * On construction of any tool, Qatch will copy the tool files inside of the language-specific Qatch run into
     * a temporary folder named 'tools'.  This handles problems with accessing executables inside of JAR files.
     *
     * @param name
     *      The tool name
     * @param toolRoot
     *      The initial location of this tool's root folder
     */
    public Tool(String name, Path toolRoot) {
        this.name = name;
        this.toolRoot = toolRoot;
        this.executable = initialize(toolRoot);
    }


    // Getters and setters
    public String getName() { return name; }
    public Path getExecutable() {
        return executable;
    }
    public Path getToolRoot() {
        return toolRoot;
    }
    public void setToolRoot(Path toolRoot) {
        this.toolRoot = toolRoot;
    }

    // Methods
    @Override
    public abstract Path initialize(Path toolRoot);
}
