package qatch.analysis;

import java.nio.file.Path;

public abstract class Tool implements ITool {

    // Instance vars
    private String name;
    private Path config;


    // Constructor
    /**
     * @param name
     *      The tool name
     */
    public Tool(String name) {
        this.name = name;
    }


    // Getters and setters
    public String getName() { return name; }

}
