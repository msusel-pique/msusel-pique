package qatch.analysis;

import org.yaml.snakeyaml.Yaml;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Path;
import java.util.*;

public abstract class Tool implements  ITool {

    // instance vars
    private String name;
    private Map<String, Set<Diagnostic>> measureMappings;

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
    public Map<String, Set<Diagnostic>> getMeasureMappings() { return measureMappings; }


    // methods
    @Override
    public Map<String, Set<Diagnostic>> mapMeasures(Path toolConfig) {

        Map<String, Set<Diagnostic>> mappings = new HashMap<>();
        Yaml yaml = new Yaml();
        try {
            Reader yamlFile = new FileReader(toolConfig.toFile());
            Map<String, Object> yamlMaps = yaml.load(yamlFile);

            yamlMaps.forEach((k, v) -> {

                Set<Diagnostic> diagnostics = new HashSet<>();
                LinkedHashMap yamlDiagnostics = (LinkedHashMap) yamlMaps.get(k);
                ArrayList<String> yamlList = (ArrayList) yamlDiagnostics.get("Diagnostics");
                yamlList.forEach(e -> {
                    Diagnostic diagnostic = new Diagnostic(this.name, e.toString());
                    diagnostics.add(diagnostic);
                });
                mappings.put(k, diagnostics);
            });

            System.out.println("...");
        }
        catch (FileNotFoundException e) { e.printStackTrace(); }

        return mappings;
    }
}
