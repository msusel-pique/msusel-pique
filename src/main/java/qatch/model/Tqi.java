package qatch.model;

import com.google.gson.annotations.Expose;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the total quality index of a project.
 * It is used for the evaluation (quality assessment - estimation) of a 
 * certain project or a benchmark of projects.
 * 
 * @author Miltos, Rice
 *
 */
public class Tqi extends ModelNode {
	
	// Fields, some inherit from super class

	@Expose
	private Map<String, Double> weights;  // mapping of characteristic names and their weights
	@Expose
	private Map<String, Characteristic> characteristics = new HashMap<>(); // mapping of characteristic names and their object


	// Getters and setters

	public Map<String, Characteristic> getCharacteristics() {
		return characteristics;
	}
	public void setCharacteristics(Map<String, Characteristic> characteristics) {
		this.characteristics = characteristics;
	}
	public double getWeight(String characteristicName) {
		return weights.get(characteristicName);
	}
	public Map<String, Double> getWeights() {
		return weights;
	}
	public void setWeights(Map<String, Double> weights) {
		this.weights = weights;
	}
	public void setWeight(String characteristicName, double value) {
		this.weights.put(characteristicName, value);
	}


	// Constructor

	public Tqi(String name, String description, Map<String, Double> weights) {
		super(name, description);
		this.weights = (weights == null) ? new HashMap<>() : weights;
	}

	public Tqi(String name, String description, Map<String, Double> weights, Map<String, Characteristic> characteristics) {
		super(name, description);
		this.weights = (weights == null) ? new HashMap<>() : weights;
		this.characteristics = characteristics;
	}


	// Methods

	@Override
	public ModelNode clone() {
		Map<String, Characteristic> clonedCharacteristics = new HashMap<>();
		getCharacteristics().values().forEach(characteristic -> {
			Characteristic clonedCharacteristic = (Characteristic)characteristic.clone();
			clonedCharacteristics.put(clonedCharacteristic.getName(), clonedCharacteristic);
		});

		return new Tqi(getName(), getDescription(), getWeights(), clonedCharacteristics);
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Tqi)) { return false; }
		Tqi otherTqi = (Tqi) other;

		return getName().equals(otherTqi.getName())
				&& getCharacteristics().size() == otherTqi.getCharacteristics().size();
	}

	@Override
	public void evaluate() {

		// assert a weight mapping exists for each provided characteristic
		getWeights().keySet().forEach(k -> {
			if (!getCharacteristics().containsKey(k)) {
				throw new RuntimeException("No weight-measure mapping in root node " + this.getName() +
						"exists for Characteristic " + k);
			}
		});

		// evaluate: standard weighted sum of characteristic values
		double sum = 0.0;
		for (Map.Entry<String, Double> weightMap : getWeights().entrySet()) {
			sum += getCharacteristics().get(weightMap.getKey()).getValue() * weightMap.getValue();
		}

		setValue(sum);
	}
}
