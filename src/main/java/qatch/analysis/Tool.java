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
    private Path config;

    // constructor
    /**
     * @param name
     *      The tool name
     */
    public Tool(String name, Path config) {
        this.name = name;
        this.config = config;
    }


    // getters and setters
    public String getName() { return name; }
    @Override
    public Path getConfig() { return config; }

    // methods
    @Override
    public Map<String, Measure> applyFindings(Map<String, Measure> measures, Map<String, Diagnostic> diagnosticFindings) {
        // update measure -> diagnostic -> finding values in mapping with any existing findings
        measures.values().forEach(measure -> {
            List<Diagnostic> diagnostics = measure.getDiagnostics();
            diagnostics.forEach(diagnostic -> {
                if (diagnosticFindings.get(diagnostic.getId()) != null) {
                    diagnostic.setFindings(diagnosticFindings.get(diagnostic.getId()).getFindings());
                }
            });
        });
        return measures;
    }

    @Override
    @SuppressWarnings("unchecked")  // TODO: deal with unchecked call warning when more time to think about it
    public Map<String, Measure> parseConfig(Path toolConfig) {

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
