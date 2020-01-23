package qatch.analysis;

import com.google.gson.annotations.Expose;
import qatch.model.ModelNode;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * A diagnostic object contains a collection of Findings that matches this object's ID and
 * defines how to evaluate its Finding collection -- for example, a count.
 *
 * A diagnostic can also be thought of as a "rule" or the ID of a finding a static analysis tool
 * attempts to find.
 *
 * Diagnostic objects are the components a Measure object needs to run its evaluation.
 *
 * Metrics example: for a metrics Measure "MIF" (Method Inheritance Factor) that is evaluated by the formula
 *          MIF = inherited methods / total methods available
 * and you have a static analysis tool that can find IM (inherited methods) and TMA (total methods available),
 * IM and TMA are Diagnostic objects, and the Measure object MIF's eval function is IM / TMA.
 *
 * Rule example: for a rule Measure "Encryption Faults" that is evaluated by the sum of all tool findings that relate
 * to encryption, imagine a tool named Security Code Scan that has 3 rules relating to encryption, "SCS001", "SCS002",
 * and "SCS003".  SCS001, SCS002, and SCS003 are Diagnostic objects whose eval function should be to sum the findings
 * with their Diagnostic name.  The Measure "Encryption Faults" would then eval by suming its diagnostic object's evals.
 */
public class Diagnostic extends ModelNode {

    // Instance variables

    private Function<Set<Finding>, Double> evalFunction;
    @Expose
    private Set<Finding> findings = new HashSet<>();
    @Expose
    private String toolName;


    // Constructors

    /**
     * Constructor with default evaluation function.
     *
     * @param id
     *      The diagnostic name as it appears in the tool's output file (e.g. "SCS0005")
     * @param description
     *      Description of the diagnostic according to its tool's or metric's documentation
     * @param toolName
     *      The name of the tool that generates this Diagnostic in its output file.  Should match the name of
     *      its associated ITool::name object
     */
    public Diagnostic(String id, String description, String toolName) {
        super(id, description);
        this.evalFunction = this::defaultEvalFunction;
        this.toolName = toolName;
    }

    /**
     * Constructor with custom evaluation function
     *
     * @param id
     *      The diagnostic name as it appears in the tool's output file (e.g. "SCS0005")
     * @param description
     *      Description of the diagnostic according to its tool's or metric's documentation
     * @param toolName
     *      The name of the tool that generates this Diagnostic in its output file.  Should match the name of
     *      its associated ITool::name object
     * @param customFunction
     *      A first class function description how to transform its collection of findings into a numerical value
     */
    public Diagnostic(String id, String description, String toolName, Function<Set<Finding>, Double> customFunction) {
        super(id, description);
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
