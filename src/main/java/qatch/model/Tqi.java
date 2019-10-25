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
	
	//The basic fields of the class
	private double eval;  //The total quality index (total quality score) of a project
	private String name;
	private Map<String, Double> weights = new HashMap<>();  // mapping of characteristic names and their weights
	@Deprecated
	private Vector<Double> weights_depreciated;	 //The weights used for the calculation of the TQI from the evals of the QM's characteristics


	//Setters and Getters
	public double getEval() {
		return eval;
	}
	public void setEval(double eval) {
		this.eval = eval;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public void calculateTQI(CharacteristicSet characteristics){
		double sum = 0;
		for(int i = 0; i < characteristics.size(); i++){
			//The number of weights is equal to the number of the model's characteristics
			//The sequence of the characteristics matters!!!!!!
			sum += characteristics.get(i).getEval() * this.weights_depreciated.get(i).doubleValue();
		}
		this.eval = sum;
	}
	
	
	//Vector methods
	public void addWeight(Double weight){
		weights_depreciated.add(weight);
	}
	
	public Double get(int index){
		return weights_depreciated.get(index);
	}
	
	public boolean isEmpty(){
		return weights_depreciated.isEmpty();
	}
	
	public Iterator<Double> iterator(){
		return weights_depreciated.iterator();
	}
	
	// Removes the first occurrence
	
	public int size(){
		return weights_depreciated.size();
	}

	//TODO: Deep Cloning - Check PropertySet class (and Property, Measure)
	@Override
	public Object clone() throws CloneNotSupportedException {
	    Tqi cloned = new Tqi();
	    for(int i = 0; i < this.weights_depreciated.size(); i++){
	    	cloned.weights_depreciated.add((Double) this.getWeights_depreciated().get(i));
	    }
		return cloned;
	}

}
