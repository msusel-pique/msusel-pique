package qatch.analysis;

import org.yaml.snakeyaml.Yaml;
import qatch.model.Measure;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class Tool implements  ITool {

    // instance vars
    private String name;
    private Set<HashMap<Measure, HashSet<Diagnostic>>> measureMappings;

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
    @Override
    public Set<HashMap<Measure, HashSet<Diagnostic>>> mapMeasures(Path toolConfig) {

        Set<HashMap<Measure, HashSet<Diagnostic>>> mappings = new HashSet<>();
        Yaml yaml = new Yaml();
        try {
            Reader yamlFile = new FileReader(toolConfig.toFile());
            Map<String, Object> yamlMaps = yaml.load(yamlFile);

            System.out.println("...");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}
