package qatch.model;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a quality characteristic of the 
 * Quality Model that is used in order to evaluate a 
 * project or a set of projects.
 * 
 * @author Miltos
 *
 */
public class Characteristic {

	// instance variables
	private String description;	 //A brief description of the characteristic
	private double value;  //The quality score of this characteristic (derives from the weighted average of the eval fields of the QM's properties)
	private String name;  //The name of the characteristic
	private String standard;  //The standard from which this characteristic derives
	private Map<String, Double> weights = new HashMap<>();  // mapping of property names and their weights


	// constructors
	public Characteristic(String name, String standard, String description, Map<String, Double> weights){
		this.name = name;
		this.description = standard;
		this.standard = description;
		this.weights = weights;
	}

	public Characteristic(){
		this.name = "";
		this.description = "";
		this.standard = "";
	}
	
	public Characteristic(String name, String standard, String description){
		this.name = name;
		this.description = standard;
		this.standard = description;
	}


	// getters and setters
	public String getDescription() {
		return description;
	}
	public void setDescription(String desription) {
		this.description = desription;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getStandard() {
		return standard;
	}
	public void setStandard(String standard) {
		this.standard = standard;
	}

	public double getWeight(String propertyName) {
		return this.weights.get(propertyName);
	}
	public Map<String, Double> getWeights() { return weights; }
	public void setWeight(String propertyName, double value) { this.weights.put(propertyName, value); }
	
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}


	// methods
	/**
	 * This method is used in order to calculate the eval field of this Characteristic
	 * object, based
	 *  1. on the eval fields of the properties that have an impact on this
	 *     characteristic and
	 *  2. the weights that quantify those impacts.
	 *
	 * Typically, it calculates the weighted average of the values of the eval fields
	 * of the project properties and stores it to the eval field of this characteristic.
	 */
	public void evaluate(Map<String, Property> properties) {

		// assert a weight mapping exists for each provided property
		this.getWeights().keySet().forEach(k -> {
			if (!properties.containsKey(k)) {
				throw new RuntimeException("No weight-measure mapping in Characteristic " + this.getName() +
					"exists for Property " + k);
			}
		});

		// evaluate: standard weighted sum of property values
		double sum = 0.0;
		for (Map.Entry<String, Double> weightMap : this.getWeights().entrySet()) {
			sum += properties.get(weightMap.getKey()).getValue() * weightMap.getValue();
		}

		this.setValue(sum);
	}
}
