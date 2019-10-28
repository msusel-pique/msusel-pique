package qatch.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

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
	private double value;  //The total quality index (total quality score) of a project
	private String name;
	private Map<String, Double> weights = new HashMap<>();  // mapping of characteristic names and their weights
	@Deprecated
	private Vector<Double> weights_depreciated;	 //The weights used for the calculation of the TQI from the evals of the QM's characteristics


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

	public Vector<Double> getWeights_depreciated() {
		return weights_depreciated;
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

	/**
	 * This method calculates the Total Quality Index (TQI), i.e. the total eval (quality score),
	 * of the project based on:
	 * 	- The values of "eval" fields of the characteristics of the model
	 *  - The values of the weights that the model assigns to each characteristic
	 *  
	 *  ATTENTION:
	 *   - The order of the Characteristic objects that are placed in the CharacteristicSet of the
	 *     quality model matters, because the weights are placed in the weights Vector depending on 
	 *     this order!
	 *   - Do not change the auto-generated order neither in the Quality Model xml file nor elsewhere 
	 *     inside the program.
	 * @param characteristics : The CharacteristicSet object with the eval fields calculated received 
	 *                          from the Project object.
	 */
	@Deprecated
	public void calculateTQI_deprecated(CharacteristicSet characteristics){
		double sum = 0;
		for(int i = 0; i < characteristics.size(); i++){
			//The number of weights is equal to the number of the model's characteristics
			//The sequence of the characteristics matters!!!!!!
			sum += characteristics.get(i).getValue() * this.weights_depreciated.get(i).doubleValue();
		}
		this.value = sum;
	}
	
	
	//Vector methods
	@Deprecated
	public void addWeight(Double weight){
		weights_depreciated.add(weight);
	}
	@Deprecated
	public Double get(int index){
		return weights_depreciated.get(index);
	}
	@Deprecated
	public boolean isEmpty(){
		return weights_depreciated.isEmpty();
	}
	@Deprecated
	public Iterator<Double> iterator(){
		return weights_depreciated.iterator();
	}
	// Removes the first occurrence
	@Deprecated
	public int size(){
		return weights_depreciated.size();
	}

}
