package qatch.analysis;

import com.google.gson.annotations.Expose;
import qatch.model.ModelNode;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * A Measure is a concrete, numerical way to measure a given Property (property of source code).
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

	private Function<List<Diagnostic>, Double> evalFunction;
	@Expose
	private List<Diagnostic> diagnostics;


	// Constructors


	// (todo): change to builder pattern

	public Measure() {
		super(null, null);
		this.evalFunction = this::defaultEvalFunction;
	}

	/**
	 * Measure constructor with default evaluation function
	 *
	 * @param name
	 * 		The Measure's name as it appears in the quality model
	 * @param description
	 * 		The measure's description as it appears in the quality model
	 * @param diagnostics
	 * 		The collection of Diagnostic objects needed for this measure's evaluation
	 */
	public Measure(String name, String description, List<Diagnostic> diagnostics) {
		super(name, description);
		this.diagnostics = diagnostics;
		this.evalFunction = this::defaultEvalFunction;
	}

	/**
	 * Measure constructor with custom evaluation function
	 *
	 * @param name
	 * 		The Measure's name as it appears in the quality model
	 * @param description
	 * 		The measure's description as it appears in the quality model
	 * @param diagnostics
	 * 		The collection of Diagnostic objects needed for this measure's evaluation
	 * @param evalFunction
	 *      A first class function description how to transform its collection of Diagnostics into a numerical value
	 */
	public Measure(String name, String description, List<Diagnostic> diagnostics, Function<List<Diagnostic>, Double> evalFunction) {
		super(name, description);
		this.diagnostics = diagnostics;
		this.evalFunction = evalFunction;
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


	// Methods

	@Override
	public ModelNode clone() {
		List<Diagnostic> clonedDiagnostics = new ArrayList<>();
		getDiagnostics().forEach(diagnostic -> {
			Diagnostic clonedDiagnostic = (Diagnostic) diagnostic.clone();
			clonedDiagnostics.add(clonedDiagnostic);
		});
		return new Measure(getName(), getDescription(), clonedDiagnostics);
	}

	/**
	 * Measures must define in their instantiating, language-specific class
	 * how to evaluate its collection of diagnostics.  Often this will simply be
	 * a count of findings, but quality evaluation (especially in the context of security)
	 * should allow for other evaluation functions.
	 *
	 * The measure value must also be normalzed according to the input argument (Likly LLoC).
	 *
	 * @param args
	 * 		A single entry representing the lines of code. This is needed for normalization.
	 */
	@Override
	protected void evaluate(Double... args) {

		if (args.length != 1) throw new RuntimeException("Measure.evaluate() expects input args of lenght 1.");

		else {
			Double loc = args[0];
			if (loc != 0) {
				assert this.evalFunction != null;
				getDiagnostics().forEach(Diagnostic::evaluate);
				Double notNormalizedValue = this.evalFunction.apply(getDiagnostics());
				double normalizedValue = notNormalizedValue/loc;
				setValue(normalizedValue);
			}
			else {
				throw new RuntimeException("Division by zero occured on metric " + getName() +
						". This is likely due to the metrics analyzer either \nfailing to get the " +
						"total lines of code, or failing to assign the TLOC to the property's measure's normalizer.");
			}
		}
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
			value += d.getValue();
		}

		return value;
	}
}
