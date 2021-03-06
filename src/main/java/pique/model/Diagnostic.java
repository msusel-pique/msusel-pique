package pique.model;

import com.google.gson.annotations.Expose;
import pique.evaluation.*;

import java.util.HashMap;
import java.util.Map;

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
    private String toolName;


    // Constructors

    public Diagnostic(String id, String description, String toolName) {
        super(id, description, new DefaultDiagnosticEvaluator(), new DefaultNormalizer());
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
     */
    public Diagnostic(String id, String description, String toolName, IEvaluator evaluator) {
        super(id, description, evaluator, new DefaultNormalizer());
        this.toolName = toolName;
    }

    public Diagnostic(String id, String description, String toolName, IEvaluator evaluator, INormalizer normalizer,
                      IUtilityFunction utilityFunction, Map<String, Double> weights, Double[] thresholds) {
        super(id, description, evaluator, normalizer, utilityFunction, weights, thresholds);
        this.toolName = toolName;
    }

    // Used for cloning
    public Diagnostic(double value, String name, String description, IEvaluator evaluator, INormalizer normalizer,
                   IUtilityFunction utilityFunction, Map<String, Double> weights, Double[] thresholds, Map<String,
            ModelNode> children) {
        super(value, name, description, evaluator, normalizer, utilityFunction, weights, thresholds, children);
    }


    // Getters and setters

    public String getToolName() {
        return toolName;
    }


    // Methods

    @Override
    public ModelNode clone() {

        Map<String, ModelNode> clonedChildren = new HashMap<>();
        getChildren().forEach((k, v) -> clonedChildren.put(k, v.clone()));

        return new Diagnostic(getValue(), getName(), getDescription(), getEvaluatorObject(), getNormalizerObject(),
                getUtilityFunctionObject(), getWeights(), getThresholds(), clonedChildren);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Diagnostic)) { return false; }
        Diagnostic otherDiagnostic = (Diagnostic) other;

        return getName().equals(otherDiagnostic.getName())
                && getToolName().equals(otherDiagnostic.getToolName());
    }
}
