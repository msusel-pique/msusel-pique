package qatch.analysis;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

public class Measure implements Cloneable {
	
	//TODO: delete these depreciated fields
	public static final int METRIC = 0;
	public static final int FINDING = 1;

	private int type;//The type of the property(METRIC or FINDING)
	private String metricName;
	private String rulesetPath;
	private String tool;
	private int normalizer;
	private double normValue;//The normalized value of it's measure (metric or findings)

	private String name;
	private String toolName;
	private List<Diagnostic> diagnostics;
	private double value;
	private double normalizedValue;

	
	public Measure(){
		//Empty constructor in order to avoid null pointer exception during quality model import...
	}

	// constructor
	public Measure(String name, String toolName, List<Diagnostic> diagnostics) {
		this.name = name;
		this.toolName = toolName;
		this.diagnostics = diagnostics;
	}
	public Measure(int type){
		if(type == METRIC || type == FINDING){
			this.type = type;
		}else{
			System.out.println("Invalid measure type! A measure can be either METRIC or FINDING!");
		}
	}
	public Measure(int type, String metricName){
		if(type == METRIC || type == FINDING){
			this.type = type;
		}else{
			System.out.println("Invalid measure type! A measure can be either METRIC or FINDING!");
		}
		this.metricName = metricName;
	}
	public Measure(int type, String metricName, String rulesetPath){
		if(type == METRIC || type == FINDING){
			this.type = type;
		}else{
			System.out.println("Invalid measure type! A measure can be either METRIC or FINDING!");
		}
		this.metricName = metricName;
		this.rulesetPath = rulesetPath;
	}
	public Measure(int type, String metricName, String rulesetPath, String toolName) {
		if(type == METRIC || type == FINDING){
			this.type = type;
		}else{
			System.out.println("Invalid measure type! A measure can be either METRIC or FINDING!");
		}
		this.metricName = metricName;
		this.rulesetPath = rulesetPath;
		this.tool = toolName;
	}


	// getters and setters
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public String getToolName() { return toolName; }
	public void setToolName(String toolName) { this.toolName = toolName; }

	public Diagnostic getDiagnostic(String id) {
		return this.diagnostics.stream()
							   .filter(d -> d.getId().equals(id))
							   .map(d -> new Diagnostic(d.getId()))
							   .findAny()
				               .orElse(null);
	}
	public List<Diagnostic> getDiagnostics() { return diagnostics; }
	public void setDiagnostics(List<Diagnostic> diagnostics) { this.diagnostics = diagnostics; }

	public double getNormalizedValue() { return normalizedValue; }
	public void setNormalizedValue(double normalizedValue) { this.normalizedValue = normalizedValue; }


	// methods
	/**
	 * Evaluate the collection of diagnostics and their associated findings
	 *
	 * @return
	 * 		The measure value after applying its evaluation function to its findings
	 */
	public double evalute() {
		throw new NotImplementedException();
	}


	// TODO: delete these depreciated methods
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public double getNormValue() {
		return normValue;
	}
	public void setNormValue(double normValue) {
		this.normValue = normValue;
	}
	public String getTool() {
		return tool;
	}
	public Measure setTool(String tool) {
		this.tool = tool;
		return this;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getMetricName() {
		return metricName;
	}
	public void setMetricName(String metricName) {
		this.metricName = metricName;
	}
	public String getRulesetPath() {
		return rulesetPath;
	}
	public void setRulesetPath(String rulesetPath) {
		this.rulesetPath = rulesetPath;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
	    return super.clone();
	}

	public int getNormalizer() {
		return normalizer;
	}

	public void setNormalizer(int normalizer) {
		this.normalizer = normalizer;
	}
	
	/**
	 * This method calculates the normalized value of this measure.
	 * It just divides the value field by the normalizer field.
	 */
	public void calculateNormValue(){
		if (this.normalizer != 0){
			this.normValue = this.value/this.normalizer;
		} else {
			throw new RuntimeException("Division by zero occured on metric " + this.metricName +
				". This is likely due to the metrics analyzer either \nfailing to get the " +
				"total lines of code, or failing to assign the TLOC to the property's measure's normalizer.");
		}
	}
}
