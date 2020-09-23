package pique.model;

import com.google.gson.annotations.Expose;
import pique.evaluation.IEvaluator;

import java.util.HashSet;
import java.util.Set;

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

    @Expose
    private String name = getName();
    @Expose
    private String eval_strategy;
    @Expose
    private String toolName;
    @Expose
    private Set<Finding> findings = new HashSet<>();
    IEvaluator evaluator;


    // Constructors
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
     */
    public Diagnostic(String id, String description, String toolName, IEvaluator evaluator) {
        super(id, description);
        this.toolName = toolName;
        this.evaluator = evaluator;
        this.eval_strategy = this.evaluator.getName();
    }


    // Getters and setters

    public IEvaluator getEvaluator() {
        return evaluator;
    }

    public String getEval_strategy() {
        return eval_strategy;
    }
    private void setEval_strategy(String eval_strategy) {
        this.eval_strategy = eval_strategy;
    }

    public Set<Finding> getFindings() { return findings; }
    public void setFinding(Finding finding) { findings.add(finding); }
    public void setFindings(Set<Finding> findings) { this.findings = findings; }

    public int getNumFindings() {
        return getFindings().size();
    }

    public String getToolName() {
        return toolName;
    }


    // Methods

    @Override
    public ModelNode clone() {
        Diagnostic clonedDiagnostic = new Diagnostic(getName(), getDescription(), getToolName(), getEvaluator());
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
        assert getEvaluator().evalStrategy() != null;
        setValue(getEvaluator().evalStrategy().apply(this.findings));
    }
}
