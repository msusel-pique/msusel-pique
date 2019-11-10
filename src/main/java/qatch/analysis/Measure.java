package qatch.analysis;

import com.google.gson.annotations.Expose;

import java.util.List;
import java.util.function.Function;

public class Measure {

	// instance variables
	private Function<List<Diagnostic>, Double> evalFunction;
	@Expose
	private String name;
	@Expose
	private double normalizedValue;
	@Expose
	private String toolName;
	@Expose
	private List<Diagnostic> diagnostics;
	@Expose
	private double value;


	// constructors
	public Measure() {
		this.evalFunction = this::defaultEvalFunction;
	}

	public Measure(String name, String toolName, List<Diagnostic> diagnostics) {
		this.name = name;
		this.toolName = toolName;
		this.diagnostics = diagnostics;
		this.evalFunction = this::defaultEvalFunction;
	}

	public Measure(String name, String toolName, List<Diagnostic> diagnostics, Function<List<Diagnostic>, Double> evalFunction) {
		this.name = name;
		this.toolName = toolName;
		this.diagnostics = diagnostics;
		this.evalFunction = evalFunction;
	}


	// getters and setters
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public String getToolName() { return toolName; }
	public void setToolName(String toolName) { this.toolName = toolName; }

	public Diagnostic getDiagnostic(String id) {
		return this.diagnostics
			.stream()
		    .filter(d -> d.getId().equals(id))
		    .findAny()
		    .orElse(null);
	}
	public List<Diagnostic> getDiagnostics() { return diagnostics; }
	public void setDiagnostics(List<Diagnostic> diagnostics) { this.diagnostics = diagnostics; }

	public double getNormalizedValue() { return normalizedValue; }
	public void setNormalizedValue(double normalizedValue) { this.normalizedValue = normalizedValue; }

	// TODO: this probably causes a bug when called multiple times on the same object
	public double getValue() {
		this.value = evaluate();
		return this.value;
	}


	// methods
	/**
	 * Measures must define in their instantiating, language-specific class
	 * how to evaluate its collection of diagnostics.  Often this will simply be
	 * a count of findings, but quality evaluation (especially in the context of security)
	 * should allow for other evaluation functions.
	 *
	 * @return
	 *      The non-normalized value of the measure
	 */
	public double evaluate() {
		assert this.evalFunction != null;
		this.getDiagnostics().forEach(Diagnostic::getValue);
		return this.evalFunction.apply(this.diagnostics);
	}


	/**
	 * This method calculates the normalized value of this measure.
	 * It just divides the value field by the normalizer field.
	 */
	public double calculateNormValue(int loc){
		if (loc != 0) {
			return this.value/loc;
		} else {
			throw new RuntimeException("Division by zero occured on metric " + this.name +
				". This is likely due to the metrics analyzer either \nfailing to get the " +
				"total lines of code, or failing to assign the TLOC to the property's measure's normalizer.");
		}
	}


	// helper methods
	/**
	 * Define the default evaluation function to simply be a count of all diagnostic findings
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
