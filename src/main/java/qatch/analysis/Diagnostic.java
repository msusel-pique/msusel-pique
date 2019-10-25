package qatch.analysis;

import java.util.HashSet;
import java.util.Set;

public class Diagnostic {

    // instance variables
    private String tool;
    private String id;
    private Set<Finding> findings = new HashSet<>();


    // constructor
    public Diagnostic(String tool, String id) {
        this.tool = tool;
        this.id = id;
    }


    // getters and setters
    public String getTool() { return tool;  }
    public String getId() { return id; }
    public Set<Finding> getFindings() { return findings; }
    public void addFinding(Finding finding) { findings.add(finding); }
    public void setFindings(Set<Finding> findings) { this.findings = findings; }
}
