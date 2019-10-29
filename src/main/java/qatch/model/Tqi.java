package qatch.model;

import com.google.gson.annotations.Expose;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the total quality index of a project.
 * It is used for the evaluation (quality assessment - estimation) of a 
 * certain project or a benchmark of projects.
 * 
 * @author Miltos
 *
 */
public class Tqi {
	
	// fields
	@Expose
	private double value;  //The total quality index (total quality score) of a project
	@Expose
	private String name;
	@Expose
	private Map<String, Double> weights = new HashMap<>();  // mapping of characteristic names and their weights

	// getters and setters
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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


	// constructor
	public Tqi() { }

	public Tqi(String name, Map<String, Double> weights) {
		this.name = name;
		this.weights = weights;
	}


	// methods
	public void evaluate(Map<String, Characteristic> characteristics) {

		// assert a weight mapping exists for each provided characteristic
		this.getWeights().keySet().forEach(k -> {
			if (!characteristics.containsKey(k)) {
				throw new RuntimeException("No weight-measure mapping in root node " + this.getName() +
						"exists for Characteristic " + k);
			}
		});

		// evaluate: standard weighted sum of characteristic values
		double sum = 0.0;
		for (Map.Entry<String, Double> weightMap : this.getWeights().entrySet()) {
			sum += characteristics.get(weightMap.getKey()).getValue() * weightMap.getValue();
		}

		this.setValue(sum);
	}
}
