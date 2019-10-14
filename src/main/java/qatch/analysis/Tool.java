package qatch.analysis;

import qatch.model.Measure;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public abstract class Tool implements  ITool {

    // instance vars
    private String name;
    private Set<HashMap<Measure, HashSet<Diagnostic>>> measureMappings = new HashSet<>();

    // constructor

    /**
     * Constructor: on creation of any tool, the name of the tool should be specified
     * and measure mapping set should be formed.
     *
     * @param name
     *      The tool name
     * @param toolConfig
     *      The path to a config file (likely .yaml) describing which measure and associated rules
     *      this tool will handle
     */
    public Tool(String name, Path toolConfig) {
        this.name = name;
        this.measureMappings = mapMeasures(toolConfig);
    }


    // getters and setters
    public String getName() { return name; }
    public Set<HashMap<Measure, HashSet<Diagnostic>>> getMeasureMappings() { return measureMappings; }


    // methods
    /**
     * Read a .yaml config file that relates properties to their associated tool, measure, and diagnostics.
     * The .yaml file should have the form
     *      Property01Name:
     *        Tool: Tool_Name
     *        Measure: Measure_Name
     *        Diagnostics:
     *          - list
     *          - of
     *          - relevant
     *          - diagnostic names
     *      Injection:
     *        Tool: Roslynator
     *        Measure: Injection Findings
     *        Diagnostics:
     *          - SCS0001
     *          - SCS0002
     *
     * @param toolConfig
     *      Path location of the .yaml configuration
     * @return
     *      The object set represetnation of mapping of this tool's associated measures
     *      and the diagnostics used to evaluate the measure
     */
    private Set<HashMap<Measure, HashSet<Diagnostic>>> mapMeasures(Path toolConfig) {



        return null;
    }
}
