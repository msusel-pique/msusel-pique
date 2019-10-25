package qatch.analysis;

import java.util.HashSet;
import java.util.Set;

public class Diagnostic {

    // instance variables
    private String id;
    private Set<Finding> findings = new HashSet<>();


    // constructor
    public Diagnostic(String id) {
        this.id = id;
    }


    // getters and setters
    public String getId() { return id; }

    public Set<Finding> getFindings() { return findings; }
    public void setFinding(Finding finding) { findings.add(finding); }
    public void setFindings(Set<Finding> findings) { this.findings = findings; }
}
