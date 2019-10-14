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

    // getters and setters
    public String getName() {
        return name;
    }
    public Set<HashMap<Measure, HashSet<Diagnostic>>> getMeasureMappings() { return measureMappings; }

    // methods
    protected void mapMeasures(Path toolConfig) {

    }
}
