package pique.model;

import com.google.gson.annotations.Expose;
import pique.calibration.DefaultBenchmarker;
import pique.calibration.IBenchmarker;
import pique.evaluation.DefaultMeasureEvaluator;
import pique.evaluation.DefaultNormalizer;
import pique.evaluation.IEvaluator;
import pique.evaluation.INormalizer;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

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
	private String normalizer;
	@Expose
	private String eval_strategy;
	@Expose
	private int num_findings = getNumFindings();
	@Expose
	private ArrayList<String> parents = new ArrayList<>();
	@Expose
	private List<Diagnostic> diagnostics;
	@Expose
	private Double[] thresholds;
	private Function<List<Diagnostic>, Double> evalFunction;
	private INormalizer iNormalizer;
	private IBenchmarker benchmarker;
	private IEvaluator evaluator;


	// Constructors
	// (todo): change to builder pattern
	public Measure(String name, String description, boolean positive, IBenchmarker benchmarker) {
		super(name, description);
		this.positive = positive;
		this.diagnostics = new ArrayList<>();
		this.evalFunction = this::defaultEvalFunction;
		this.iNormalizer = new DefaultNormalizer();
		this.benchmarker = benchmarker;
		this.evaluator = new DefaultMeasureEvaluator();

		this.normalizer = this.iNormalizer.getNormalizerName();
		this.eval_strategy = this.evaluator.getName();
	}

	public Measure(String name, String description, boolean positive, List<Diagnostic> diagnostics, IBenchmarker benchmarker) {
		super(name, description);
		this.positive = positive;
		this.diagnostics = diagnostics;
		this.iNormalizer = new DefaultNormalizer();
		this.evalFunction = this::defaultEvalFunction;
		this.benchmarker = benchmarker;
		this.evaluator = new DefaultMeasureEvaluator();

		this.normalizer = this.iNormalizer.getNormalizerName();
		this.eval_strategy = this.evaluator.getName();
	}

	public Measure(String name, String description, boolean positive, List<Diagnostic> diagnostics, IBenchmarker benchmarker, Double[] thresholds) {
		super(name, description);
		this.thresholds = thresholds;
		this.positive = positive;
		this.diagnostics = diagnostics;
		this.iNormalizer = new DefaultNormalizer();
		this.evalFunction = this::defaultEvalFunction;
		this.benchmarker = benchmarker;
		this.evaluator = new DefaultMeasureEvaluator();

		this.normalizer = this.iNormalizer.getNormalizerName();
		this.eval_strategy = this.evaluator.getName();
	}

	public Measure(String name, String description, boolean positive, List<Diagnostic> diagnostics, IBenchmarker benchmarker, Double[] thresholds, INormalizer iNormalizer) {
		super(name, description);
		this.thresholds = thresholds;
		this.positive = positive;
		this.diagnostics = diagnostics;
		this.iNormalizer = iNormalizer;
		this.evalFunction = this::defaultEvalFunction;
		this.benchmarker = benchmarker;
		this.evaluator = new DefaultMeasureEvaluator();

		this.normalizer = this.iNormalizer.getNormalizerName();
		this.eval_strategy = this.evaluator.getName();
	}

	public Measure(String name, String description, boolean positive, ArrayList<String> parents, List<Diagnostic> diagnostics, IBenchmarker benchmarker, Double[] thresholds, INormalizer iNormalizer, Function<List<Diagnostic>, Double> evalFunction, IEvaluator evaluator) {
		super(name, description);
		this.thresholds = thresholds;
		this.positive = positive;
		this.parents = parents;
		this.diagnostics = diagnostics;
		this.iNormalizer = iNormalizer;
		this.evalFunction = evalFunction;
		this.benchmarker = benchmarker;
		this.evaluator = evaluator;

		this.normalizer = this.iNormalizer.getNormalizerName();
		this.eval_strategy = this.evaluator.getName();
	}


	// Getters and setters
	public IBenchmarker getBenchmarker() {
		return benchmarker;
	}

	public Diagnostic getDiagnostic(String name) {
		return this.diagnostics
			.stream()
		    .filter(d -> d.getName().equals(name))
		    .findAny()
		    .orElse(null);
	}
	public List<Diagnostic> getDiagnostics() { return diagnostics; }
	public void setDiagnostics(List<Diagnostic> diagnostics) { this.diagnostics = diagnostics; }

	public Function<List<Diagnostic>, Double> getEvalFunction() {
		return evalFunction;
	}

	public IEvaluator getEvaluator() {
		return evaluator;
	}
	public void setEvaluator(IEvaluator evaluator) {
		this.evaluator = evaluator;
	}

	public INormalizer getiNormalizer() {
		return iNormalizer;
	}
	public void setiNormalizer(INormalizer iNormalizer) {
		this.iNormalizer = iNormalizer;
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


	// Methods

	@Override
	public ModelNode clone() {
		List<Diagnostic> clonedDiagnostics = new ArrayList<>();
		getDiagnostics().forEach(diagnostic -> {
			Diagnostic clonedDiagnostic = (Diagnostic) diagnostic.clone();
			clonedDiagnostics.add(clonedDiagnostic);
		});
		return new Measure(getName(), getDescription(), isPositive(), getParents(), clonedDiagnostics, getBenchmarker(), getThresholds(), getiNormalizer(), getEvalFunction(), getEvaluator());
	}

	/**
	 * Measures must define in their instantiating, language-specific class
	 * how to evaluate its collection of diagnostics.  Often this will simply be
	 * a count of findings, but quality evaluation (especially in the context of security)
	 * should allow for other evaluation functions.
	 *
	 * The measure value must also be normalzed according to the input argument (Likly LLoC).
	 *
	 * Measure evaluation formula: value = utilityFunction(normalize(evalFunction(diagnostics)))
	 *
	 * @param args
	 * 		A single entry representing the lines of code. This is needed for normalization.
	 */
	@Override
	protected void evaluate(Double... args) {

//		assert getEvaluator().evalStrategy() != null;
//		setValue(getEvaluator().evalStrategy().apply(this.findings));

		// evalFunction(diagnostics)
		assert this.evalFunction != null;
		getDiagnostics().forEach(Diagnostic::evaluate);
		Double notNormalizedValue = this.evalFunction.apply(getDiagnostics());

		// normalize
		double output = getiNormalizer().normalize(notNormalizedValue, getDiagnostic(getiNormalizer().getNormalizerDiagnosticName()));

		// utilityFunction
		if (getThresholds() != null) {
			output = getBenchmarker().utilityFunction(output, getThresholds(), isPositive());
		}

		// (todo) temp fix
		if (getDiagnostic("RCS1206") != null) {
			output = output * getEvaluator().evalStrategy().apply(getDiagnostic("RCS1206").getFindings());
		}
		else {
			output = output * getEvaluator().evalStrategy().apply(null);
		}
		// set value
		setValue(output);
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Measure)) { return false; }
		Measure otherMeasure = (Measure) other;

		return getName().equals(otherMeasure.getName())
				&& (getDiagnostics().size() == otherMeasure.getDiagnostics().size());
	}


	// Helper methods

	/**
	 * Define the default evaluation function to be a sum of all diagnostic values
	 *
	 * @param diagnostics
	 *      The list of diagnostics belonging to the measure
	 * @return
	 *      The count of findings within all diagnostics
	 */
	private double defaultEvalFunction(List<Diagnostic> diagnostics) {
		double value = 0;
		for (Diagnostic d : diagnostics) {
			if (!d.getName().equals("loc")) value += d.getValue();
		}

		return value;
	}
}
