package qatch.analysis;

import com.google.gson.annotations.Expose;
import qatch.model.ModelNode;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class Diagnostic extends ModelNode {

    // Instance variables

    private Function<Set<Finding>, Double> evalFunction;
    @Expose
    private Set<Finding> findings = new HashSet<>();
    @Expose
    private String toolName;


    // Constructors

    public Diagnostic(String id, String description, String toolName) {
        super(id, description);
        this.evalFunction = this::defaultEvalFunction;
        this.toolName = toolName;
    }

    public Diagnostic(String id, String description, String toolName, Function<Set<Finding>, Double> customFunction) {
        super(id, description);
        this.evalFunction = this::defaultEvalFunction;
        this.toolName = toolName;
        this.evalFunction = customFunction;
    }


    // Getters and setters

    public Set<Finding> getFindings() { return findings; }
    public void setFinding(Finding finding) { findings.add(finding); }
    public void setFindings(Set<Finding> findings) { this.findings = findings; }
    public String getToolName() {
        return toolName;
    }
    public void setToolName(String toolName) {
        this.toolName = toolName;
    }


    // Methods

    @Override
    public ModelNode clone() {
        Diagnostic clonedDiagnostic = new Diagnostic(getName(), getDescription(), getToolName());
        findings.forEach(finding -> {
            setFinding(finding.clone());
        });

        return clonedDiagnostic;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Diagnostic)) { return false; }
        Diagnostic otherDiagnostic = (Diagnostic) other;

        return getName().equals(otherDiagnostic.getName())
                && getToolName().equals(otherDiagnostic.getToolName());
    }

    /**
     * Diagnostics must define in their instantiating, language-specific class
     * how to evaluate the collection of its findings.  Often this will simply be
     * a count of findings, but quality evaluation (especially in the context of security)
     * should allow for other evaluation functions.
     *
     * @param args
     *      Empty args. No parameters needed.
     */
    @Override
    protected void evaluate(Double... args) {
        assert this.evalFunction != null;
        setValue(this.evalFunction.apply(this.findings));

    }


    // Helper methods

    /**
     * Define the default evaluation function to simply be a sum of (finding * findingSeverity)
     * @param findings
     *      The set of findings found by this diagnostic
     * @return
     *      The count of findings
     */
    private double defaultEvalFunction(Set<Finding> findings) {
        return findings.stream().mapToDouble(Finding::getSeverity).sum();
    }
}
