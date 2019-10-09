package qatch.analysis;

import java.util.Dictionary;
import java.util.Hashtable;

/**
 * AnalysisResult is a data object representation of static analysis results.
 */
public class AnalylsisResult {

    private String toolName;
    private Dictionary<String, Float> findings = new Hashtable<>(); // Key: associated property, Value: value from tool


    public String getToolName() {
        return toolName;
    }
    public void setToolName(String toolName) {
        this.toolName = toolName;
    }
    public float getMeasureValue(String propertyName) {
        return findings.get(propertyName);
    }
    public void addFinding(String propertyName, float value) {
        findings.put(propertyName, value);
    }
}
