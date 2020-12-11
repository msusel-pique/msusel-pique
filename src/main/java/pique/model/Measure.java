package pique.model;

import com.google.gson.annotations.Expose;
import pique.evaluation.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashMap;
import java.util.Map;

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

	//region Instance variables

	@Expose
	private boolean positive;
	@Expose
	private String normalizer_name;
	@Expose
	private String eval_strategy;
	@Expose
	private int num_findings = getNumChildren();
	@Expose
	private Double[] thresholds;

	private IUtilityFunction utilityFunction;

	//endregion


	//region Constructors
	// (todo): change to builder pattern, or upgrade to java 9+: the better option for optional method parameters :)
	public Measure(String name, String description, INormalizer normalizer, boolean positive) {
		super(name, description, new DefaultMeasureEvaluator(), normalizer);
		this.positive = positive;
		this.utilityFunction = new DefaultUtility();

		this.normalizer_name = this.normalizer.getName();
		this.eval_strategy = this.evaluator.getName();
	}

	public Measure(String name, String description, INormalizer normalizer, boolean positive,
				   Map<String, ModelNode> diagnostics) {
		super(name, description, new DefaultMeasureEvaluator(), normalizer);
		this.positive = positive;
		this.children = diagnostics;

		this.normalizer_name = this.normalizer.getName();
		this.eval_strategy = this.evaluator.getName();
	}

	public Measure(String name, String description, INormalizer normalizer, boolean positive,
				   Map<String, ModelNode> diagnostics, Double[] thresholds) {
		super(name, description, new DefaultMeasureEvaluator(), normalizer);
		this.thresholds = thresholds;
		this.positive = positive;
		this.children = diagnostics;

		this.normalizer_name = this.normalizer.getName();
		this.eval_strategy = this.evaluator.getName();
	}

	public Measure(String name, String description, boolean positive, INormalizer normalizer,
				   Map<String, ModelNode> diagnostics, Double[] thresholds) {
		super(name, description, new DefaultMeasureEvaluator(), normalizer);
		this.thresholds = thresholds;
		this.positive = positive;
		this.children = diagnostics;

		this.normalizer_name = this.normalizer.getName();
		this.eval_strategy = this.evaluator.getName();
	}


	public Measure(String name, String description, INormalizer normalizer, boolean positive,
				   Map<String, ModelNode> diagnostics, Double[] thresholds, IEvaluator evaluator) {
		super(name, description, evaluator, normalizer);
		this.thresholds = thresholds;
		this.positive = positive;
		this.children = diagnostics;

		this.normalizer_name = this.normalizer.getName();
		this.eval_strategy = this.evaluator.getName();
	}

	public Measure(String name, String description, INormalizer normalizer, boolean positive,
				   Map<String, ModelNode> diagnostics, Double[] thresholds, IEvaluator evaluator,
				   IUtilityFunction utilityFunction)
	{
		super(name, description, evaluator, normalizer);
		this.positive = positive;
		this.children = diagnostics;
		this.thresholds = thresholds;
		this.utilityFunction = utilityFunction;

		this.normalizer_name = this.normalizer.getName();
		this.eval_strategy = this.evaluator.getName();
	}

	public Measure(double value, String name, String description, IEvaluator evaluator, INormalizer normalizer,
					  Map<String, ModelNode> children, Map<String, Double> weights, boolean positive,
				   String normalizer_name,  String eval_strategy, Double[] thresholds, IUtilityFunction utilityFunction) {
		super(value, name, description, evaluator, normalizer, children, weights);

		this.positive = positive;
		this.normalizer_name = normalizer_name;
		this.eval_strategy = eval_strategy;
		this.thresholds = thresholds;
		this.utilityFunction = utilityFunction;
	}

	//endregion


	//region Getters and setters

	public IEvaluator getEvaluator() {
		return evaluator;
	}

	public void setEvaluator(IEvaluator evaluator) {
		this.evaluator = evaluator;
	}

	public void setNum_findings(int num_findings) { this.num_findings = num_findings; }

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

	public IUtilityFunction getUtilityFunction() {
		return utilityFunction;
	}

	public void setUtilityFunction(IUtilityFunction utilityFunction) {
		this.utilityFunction = utilityFunction;
	}

	//endregion


	//region Methods

	@Override
	public ModelNode clone() {
		Map<String, ModelNode> clonedChildren = new HashMap<>();
		getChildren().forEach((k, v) -> clonedChildren.put(k, v.clone()));

		return new Measure(getValue(), getName(), getDescription(), getEvaluator(), getNormalizer(),
				clonedChildren, getWeights(), isPositive(), getNormalizer_name(), getEval_strategy(),
				getThresholds(), getUtilityFunction());
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Measure)) { return false; }
		Measure otherMeasure = (Measure) other;

		return getName().equals(otherMeasure.getName())
				&& (getChildren().size() == otherMeasure.getChildren().size());
	}

	//endregion
}
