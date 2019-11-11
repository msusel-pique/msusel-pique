package qatch.model;

import com.google.gson.annotations.Expose;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a quality characteristic of the 
 * Quality Model that is used in order to evaluate a 
 * project or a set of projects.
 * 
 * @author Miltos, Rice
 *
 */
public class Characteristic extends ModelNode {

	// Instance variables
	private String standard;  //The standard from which this characteristic derives
	// TODO: eventually consider new tree object that combines properties and their weight instead of relying on String name matching (not enough time for me to refactor currently)
	private Map<String, Property> properties = new HashMap<>();  // mapping of property names and their property objects
	@Expose
	private Map<String, Double> weights = new HashMap<>();  // mapping of property names and their weights


	// Constructors
	public Characteristic(String name, String description, String standard, Map<String, Double> weights){
		super(name, description);
		this.standard = description;
		this.weights = weights;
	}
	
	public Characteristic(String name, String description, String standard){
		super(name, description);
		this.standard = standard;
	}


	// getters and setters

	public Map<String, Property> getProperties() {
		return properties;
	}
	public void setProperties(Map<String, Property> properties) {
		this.properties = properties;
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
	@Override
	public void evaluate() {
		// assert a weight mapping exists for each provided property
		getWeights().keySet().forEach(k -> {
			if (!getProperties().containsKey(k)) {
				throw new RuntimeException("No weight-measure mapping in Characteristic " + getName() +
						"exists for Property " + k);
			}
		});

		// evaluate: standard weighted sum of property values
		double sum = 0.0;
		for (Map.Entry<String, Double> weightMap : getWeights().entrySet()) {
			sum += getProperties().get(weightMap.getKey()).getValue() * weightMap.getValue();
		}

		this.setValue(sum);
	}
}
