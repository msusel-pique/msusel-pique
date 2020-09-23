package pique.model;

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
public class QualityAspect extends ModelNode {

	// Instance variables

	// TODO: eventually consider new tree object that combines properties and their weight instead of relying on String name matching (not enough time for me to refactor currently)
	private Map<String, ProductFactor> productFactors = new HashMap<>();  // mapping of productFactors names and their property objects
	@Expose
	private Map<String, Double> weights = new HashMap<>();  // mapping of productFactors names and their weights



	// Constructors

	public QualityAspect(String name, String description){
		super(name, description);
	}

	public QualityAspect(String name, String description, Map<String, Double> weights){
		super(name, description);
		this.weights = weights;
	}

	public QualityAspect(String name, String description, Map<String, Double> weights, Map<String, ProductFactor> productFactors){
		super(name, description);
		this.weights = weights;
		this.productFactors = productFactors;
	}


	// Getters and setters

	public Map<String, ProductFactor> getProductFactors() {
		return productFactors;
	}
	public void setProductFactor(ProductFactor productFactor) {
		getProductFactors().put(productFactor.getName(), productFactor);
	}
	public void setProductFactors(Map<String, ProductFactor> productFactors) {
		this.productFactors = productFactors;
	}
	public double getWeight(String productFactorName) {
		return this.weights.get(productFactorName);
	}
	public Map<String, Double> getWeights() { return weights; }
	public void setWeight(String productFactorName, double value) { this.weights.put(productFactorName, value); }
	public void setWeights(Map<String, Double> weights) {
		this.weights = weights;
	}


	// Methods

	@Override
	public ModelNode clone() {
		System.out.println("--- WARNING ---\nCloning QualityAspect node without cloned property map.\n" +
				"This will result in an empty properties field for this QualityAspect object.");
		return new QualityAspect(getName(), getDescription(), getWeights(), new HashMap<>());
	}


	public ModelNode clone(Map<String, ProductFactor> clonedProductFactors) {
		return new QualityAspect(getName(), getDescription(), getWeights(), clonedProductFactors);
	}


	@Override
	public boolean equals(Object other) {
		if (!(other instanceof QualityAspect)) { return false; }
		QualityAspect otherQualityAspect = (QualityAspect) other;

		return getName().equals(otherQualityAspect.getName())
				&& getProductFactors().size() == otherQualityAspect.getProductFactors().size();
	}


	/**
	 * This method is used in order to calculate the eval field of this QualityAspect
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

		if (args.length != 1) throw new RuntimeException("QualityAspect.evaluate() expects input args of length 1.");

		Double loc = args[0];

		// assert a weight mapping exists for each provided property
		getWeights().keySet().forEach(k -> {
			if (!getProductFactors().containsKey(k)) {
				throw new RuntimeException("No weight-measure mapping in QualityAspect " + getName() +
						" exists for ProductFactor " + k);
			}
		});

		// evaluate: standard weighted sum of property values
		double sum = 0.0;
		for (Map.Entry<String, Double> weightMap : getWeights().entrySet()) {
			sum += getProductFactors().get(weightMap.getKey()).getValue(loc) * weightMap.getValue();
		}

		this.setValue(sum);
	}
}
