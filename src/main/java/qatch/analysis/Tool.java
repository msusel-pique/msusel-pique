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
    private Map<String, Diagnostic> diagnostics = new HashMap<>();
    private Map<String, Measure> measureMappings;

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
    public void addDiagnostic(Diagnostic diagnostic) { diagnostics.put(diagnostic.getId(), diagnostic); }
    public void setDiagnostics(Map<String, Diagnostic> diagnostics) { this.diagnostics = diagnostics; }
    public Map<String, Measure> getMeasureMappings() { return measureMappings; }
    public void setMeasureMappings(Map<String, Measure> measureMappings) { this.measureMappings = measureMappings; }

    // methods
    @Override
    // TODO: probably make private, return void, change name (updateMeasures? linkMeasuresToFindings?). Maybe change mapMeasures approach instead.
    public Map<String, Measure> buildMeasures() {

        // update measure -> diagnostic -> measure values in mapping with any existing findings
        this.measureMappings.values().forEach(measure -> {
            List<Diagnostic> diagnostics = measure.getDiagnostics();
            diagnostics.forEach(diagnostic -> {
                if (this.diagnostics.get(diagnostic.getId()) != null) {
                    diagnostic.setFindings(this.diagnostics.get(diagnostic.getId()).getFindings());
                }
            });
        });

        return this.measureMappings;
    }


    @Override
    @SuppressWarnings("unchecked")  // TODO: deal with unchecked call warning when more time to think about it
    public Map<String, Measure> mapMeasures(Path toolConfig) {

        Map<String, Measure> mappings = new HashMap<>();
        Yaml yaml = new Yaml();
        try {
            Reader yamlFile = new FileReader(toolConfig.toFile());
            Map<String, Object> yamlMaps = yaml.load(yamlFile);
            Set<String> keys = yamlMaps.keySet();

            keys.forEach(k -> {
                List<Diagnostic> diagnostics = new ArrayList<>();
                LinkedHashMap yamlNestedData = (LinkedHashMap) yamlMaps.get(k);
                ArrayList yamlDiagnostics = (ArrayList) yamlNestedData.get("Diagnostics");
                yamlDiagnostics.forEach(e -> {
                    Diagnostic diagnostic = new Diagnostic(this.name, e.toString());
                    diagnostics.add(diagnostic);
                });
                Measure measure = new Measure((String) yamlNestedData.get("Measure"), this.name, diagnostics);
                mappings.put(k, measure);
            });
        }
        catch (FileNotFoundException e) { e.printStackTrace(); }

        return mappings;
    }
}
