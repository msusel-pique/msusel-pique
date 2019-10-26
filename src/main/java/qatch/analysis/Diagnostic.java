package qatch.analysis;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class Diagnostic {

    // instance variables
    private Function<Set<Finding>, Double> evalFunction;
    private Set<Finding> findings = new HashSet<>();
    private String id;
    private double value;


    // constructors
    public Diagnostic(String id) {
        this.id = id;
        this.evalFunction = this::defaultEvalFunction;
    }

    public Diagnostic(String id, Function<Set<Finding>, Double> evalFunction) {
        this.id = id;
        this.evalFunction = evalFunction;
    }


    // getters and setters
    public String getId() { return id; }

    public Set<Finding> getFindings() { return findings; }
    public void setFinding(Finding finding) { findings.add(finding); }
    public void setFindings(Set<Finding> findings) { this.findings = findings; }

    public double getValue() {
        this.value = evaluate();
        return this.value;
    }


    // methods
    /**
     * Diagnostics must define in their instantiating, language-specific class
     * how to evaluate the collection of its findings.  Often this will simply be
     * a count of findings, but quality evaluation (especially in the context of security)
     * should allow for other evaluation functions.
     *
     * @return
     *      The non-normalized value of the diagnostic
     */
    private double evaluate() {
        assert this.evalFunction != null;
        return this.evalFunction.apply(this.findings);
    }


    // helper methods
    /**
     * Define the default evaluation function to simply be a count of findings
     * @param numFindings
     *      The set of findings found by this diagnostic
     * @return
     *      The count of findings
     */
    private double defaultEvalFunction(Set<Finding> numFindings) {
        return numFindings.size();
    }
}
