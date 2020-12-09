package pique.model;

import com.google.gson.annotations.Expose;
import pique.evaluation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A Measure is a concrete, numerical way to measure a given ProductFactor (property of source code).
 * The components necessary to complete a Measure's evaluation are a Measure's Diagnostics.
 *
 * A Measure is therefore a collection of its necessary diagnostics and an evaluation function describing
 * how to evaluate itself using its diagnostics.
 *
 * Metrics example: for a metrics Measure "MIF" (Method Inheritance Factor) that is evaluated by the formula
 * 							MIF = inherited methods / total methods available
 *  	and you have a static analysis tool that can find IM (inherited methods) and TMA (total methods available),
 *  	IM and TMA are Diagnostic objects, and the Measure object MIF's eval function is IM::value / TMA::value.
 *
 *  Rule example: for a rule Measure "Encryption Faults" that is evaluated by the sum of all tool findings that relate
 *  	to encryption, imagine a tool named Security Code Scan that has 3 rules relating to encryption, "SCS001", "SCS002",
 *  	and "SCS003".  SCS001, SCS002, and SCS003 are Diagnostic objects whose eval function should be to sum the
 *  	findings with their Diagnostic name.  The Measure "Encryption Faults" may then eval by suming its diagnostic
 *  	object's evals.
 */
public class Measure extends ModelNode {

	// Instance variables
	@Expose
	private boolean positive;

	@Expose
	private String normalizer_name;
	@Expose
	private String eval_strategy;
	@Expose
	private int num_findings = getNumFindings();
	@Expose
	private ArrayList<String> parents = new ArrayList<>();
	@Expose
	private List<Diagnostic> diagnostics = new ArrayList<>();
	@Expose
	private Double[] thresholds;

	private IUtilityFunction utilityFunction;


	// Constructors
	// (todo): change to builder pattern, or upgrade to java 9+: the better option for optional method parameters :)
	public Measure(String name, String description, INormalizer normalizer, boolean positive) {
		super(name, description, new DefaultMeasureEvaluator(), normalizer);
		this.positive = positive;
		this.diagnostics = new ArrayList<>();
		this.utilityFunction = new DefaultUtility();

		this.normalizer_name = this.normalizer.getNormalizerName();
		this.eval_strategy = this.evaluator.getName();
	}

	public Measure(String name, String description, INormalizer normalizer, boolean positive,
				   List<Diagnostic> diagnostics) {
		super(name, description, new DefaultMeasureEvaluator(), normalizer);
		this.positive = positive;
		this.diagnostics = diagnostics;

		this.normalizer_name = this.normalizer.getNormalizerName();
		this.eval_strategy = this.evaluator.getName();
	}

	public Measure(String name, String description, INormalizer normalizer, boolean positive,
				   List<Diagnostic> diagnostics,
				   Double[] thresholds) {
		super(name, description, new DefaultMeasureEvaluator(), normalizer);
		this.thresholds = thresholds;
		this.positive = positive;
		this.diagnostics = diagnostics;

		this.normalizer_name = this.normalizer.getNormalizerName();
		this.eval_strategy = this.evaluator.getName();
	}

	public Measure(String name, String description, boolean positive, INormalizer normalizer,
				   List<Diagnostic> diagnostics, Double[] thresholds) {
		super(name, description, new DefaultMeasureEvaluator(), normalizer);
		this.thresholds = thresholds;
		this.positive = positive;
		this.diagnostics = diagnostics;

		this.normalizer_name = this.normalizer.getNormalizerName();
		this.eval_strategy = this.evaluator.getName();
	}


	public Measure(String name, String description, INormalizer normalizer, boolean positive, ArrayList<String> parents,
				   List<Diagnostic> diagnostics, Double[] thresholds, IEvaluator evaluator) {
		super(name, description, evaluator, normalizer);
		this.thresholds = thresholds;
		this.positive = positive;
		this.parents = parents;
		this.diagnostics = diagnostics;

		this.normalizer_name = this.normalizer.getNormalizerName();
		this.eval_strategy = this.evaluator.getName();
	}

	public Measure(String name, String description, INormalizer normalizer, boolean positive, ArrayList<String> parents,
				   List<Diagnostic> diagnostics, Double[] thresholds, IEvaluator evaluator,
				   IUtilityFunction utilityFunction)
	{
		super(name, description, evaluator, normalizer);
		this.positive = positive;
		this.parents = parents;
		this.diagnostics = diagnostics;
		this.thresholds = thresholds;
		this.utilityFunction = utilityFunction;

		this.normalizer_name = this.normalizer.getNormalizerName();
		this.eval_strategy = this.evaluator.getName();
	}

	// Getters and setters

	public Diagnostic getDiagnostic(String name) {
		return this.diagnostics
			.stream()
		    .filter(d -> d.getName().equals(name))
		    .findAny()
		    .orElse(null);
	}

	public List<Diagnostic> getDiagnostics() { return diagnostics; }

	public void setDiagnostics(List<Diagnostic> diagnostics) { this.diagnostics = diagnostics; }

	public IEvaluator getEvaluator() {
		return evaluator;
	}

	public void setEvaluator(IEvaluator evaluator) {
		this.evaluator = evaluator;
	}

	public int getNumFindings() {
		if (getDiagnostics() == null) return 0;
		else {
			return getDiagnostics().stream().mapToInt(Diagnostic::getNumFindings).sum();
		}
	}

	public void setNum_findings(int num_findings) { this.num_findings = num_findings; }

	public ArrayList<String> getParents() {
		return parents;
	}

	public void setParent(String parentName) {
		getParents().add(parentName);
	}

	public void setParents(ArrayList<String> parents) {
		this.parents = parents;
	}

	public boolean isPositive() {
		return positive;
	}

	public void setPositive(boolean positive) {
		this.positive = positive;
	}

	public Double[] getThresholds() {
		return thresholds;
	}

	public void setThresholds(Double[] thresholds) {
		this.thresholds = thresholds;
	}

	public String getNormalizer_name() {
		return normalizer_name;
	}

	public void setNormalizer_name(String normalizer_name) {
		this.normalizer_name = normalizer_name;
	}

	public String getEval_strategy() {
		return eval_strategy;
	}

	public void setEval_strategy(String eval_strategy) {
		this.eval_strategy = eval_strategy;
	}

	public int getNum_findings() {
		return num_findings;
	}

	public IUtilityFunction getUtilityFunction() {
		return utilityFunction;
	}

	public void setUtilityFunction(IUtilityFunction utilityFunction) {
		this.utilityFunction = utilityFunction;
	}

	// Methods

	@Override
	public ModelNode clone() {
		List<Diagnostic> clonedDiagnostics = new ArrayList<>();
		getDiagnostics().forEach(diagnostic -> {
			Diagnostic clonedDiagnostic = (Diagnostic) diagnostic.clone();
			clonedDiagnostics.add(clonedDiagnostic);
		});
		return new Measure(getName(), getDescription(), getNormalizer(), isPositive(), getParents(),
				clonedDiagnostics, getThresholds(), getEvaluator(), getUtilityFunction());
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Measure)) { return false; }
		Measure otherMeasure = (Measure) other;

		return getName().equals(otherMeasure.getName())
				&& (getDiagnostics().size() == otherMeasure.getDiagnostics().size());
	}
}
