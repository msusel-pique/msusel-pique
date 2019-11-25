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

	// TODO: eventually consider new tree object that combines properties and their weight instead of relying on String name matching (not enough time for me to refactor currently)
	private Map<String, Property> properties = new HashMap<>();  // mapping of property names and their property objects
	@Expose
	private Map<String, Double> weights = new HashMap<>();  // mapping of property names and their weights



	// Constructors

	public Characteristic(String name, String description){
		super(name, description);
	}

	public Characteristic(String name, String description, Map<String, Double> weights){
		super(name, description);
		this.weights = weights;
	}

	public Characteristic(String name, String description, Map<String, Double> weights, Map<String, Property> properties){
		super(name, description);
		this.weights = weights;
		this.properties = properties;
	}


	// Getters and setters

	public Map<String, Property> getProperties() {
		return properties;
	}
	public void setProperty(Property property) {
		getProperties().put(property.getName(), property);
	}
	public void setProperties(Map<String, Property> properties) {
		this.properties = properties;
	}
	public double getWeight(String propertyName) {
		return this.weights.get(propertyName);
	}
	public Map<String, Double> getWeights() { return weights; }
	public void setWeight(String propertyName, double value) { this.weights.put(propertyName, value); }
	public void setWeights(Map<String, Double> weights) {
		this.weights = weights;
	}


	// Methods
	@Override
	public ModelNode clone() {
		System.out.println("--- WARNING ---\nCloning Characteristic node without cloned property map.\n" +
				"This will result in an empty properties field for this Characteristic object.");
		return new Characteristic(getName(), getDescription(), getWeights(), new HashMap<>());
	}

	public ModelNode clone(Map<String, Property> clonedProperties) {
		return new Characteristic(getName(), getDescription(), getWeights(), clonedProperties);
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Characteristic)) { return false; }
		Characteristic otherCharacteristic = (Characteristic) other;

		return getName().equals(otherCharacteristic.getName())
				&& getProperties().size() == otherCharacteristic.getProperties().size();
	}

	/**
	 * This method is used in order to calculate the eval field of this Characteristic
	 * object, based
	 *  1. on the eval fields of the properties that have an impact on this
	 *     characteristic and
	 *  2. the weights that quantify those impacts.
	 *
	 * Typically, it calculates the weighted average of the values of the eval fields
	 * of the project properties and stores it to the eval field of this characteristic.
	 *
	 * @param args
	 * 		Empty args. No parameters needed.
	 */
	@Override
	protected void evaluate(Double... args) {

		if (args.length != 1) throw new RuntimeException("Characteristic.evaluate() expects input args of length 1.");

		Double loc = args[0];

		// assert a weight mapping exists for each provided property
		getWeights().keySet().forEach(k -> {
			if (!getProperties().containsKey(k)) {
				throw new RuntimeException("No weight-measure mapping in Characteristic " + getName() +
						" exists for Property " + k);
			}
		});

		// evaluate: standard weighted sum of property values
		double sum = 0.0;
		for (Map.Entry<String, Double> weightMap : getWeights().entrySet()) {
			sum += getProperties().get(weightMap.getKey()).getValue(loc) * weightMap.getValue();
		}

		this.setValue(sum);
	}
}
