package qatch.analysis;

import com.google.gson.annotations.Expose;
import qatch.model.ModelNode;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Measure extends ModelNode {

	// Instance variables

	private Function<List<Diagnostic>, Double> evalFunction;
	@Expose
	private List<Diagnostic> diagnostics;


	// Constructors

	public Measure(String name, String description, List<Diagnostic> diagnostics) {
		super(name, description);
		this.diagnostics = diagnostics;
		this.evalFunction = this::defaultEvalFunction;
	}

	public Measure() {
		super(null, null);
		this.evalFunction = this::defaultEvalFunction;
	}

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
	 * Define the default evaluation function to simply be a count of all diagnostic findings
	 *
	 * @param diagnostics
	 *      The list of diagnostics belonging to the measure
	 * @return
	 *      The count of findings within all diagnostics
	 */
	private double defaultEvalFunction(List<Diagnostic> diagnostics) {
		double value = 0;
		for (Diagnostic d : diagnostics) {
			for (Finding f : d.getFindings()) {
				value++;
			}
		}

		return value;
	}
}
