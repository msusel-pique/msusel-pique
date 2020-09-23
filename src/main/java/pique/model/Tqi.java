package pique.model;

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
	private Map<String, Double> weights;  // mapping of quality aspects names and their weights
	private Map<String, QualityAspect> qualityAspects = new HashMap<>(); // mapping of characteristic names and their object


	// Getters and setters

	public QualityAspect getAnyQualityAspect() {
		QualityAspect anyQualityAspect = getQualityAspects().values().stream().findAny().orElse(null);
		assert anyQualityAspect != null;
		return anyQualityAspect;
	}
	public Map<String, QualityAspect> getQualityAspects() {
		return qualityAspects;
	}
	public void setQualityAspect(QualityAspect qualityAspect) {
		getQualityAspects().put(qualityAspect.getName(), qualityAspect);
	}
	public void setQualityAspects(Map<String, QualityAspect> qualityAspects) {
		this.qualityAspects = qualityAspects;
	}
	public double getWeight(String qualityAspectName) {
		return weights.get(qualityAspectName);
	}
	public Map<String, Double> getWeights() {
		return weights;
	}
	public void setWeights(Map<String, Double> weights) {
		this.weights = weights;
	}
	public void setWeight(String qualityAspectName, double value) {
		this.weights.put(qualityAspectName, value);
	}


	// Constructor

	public Tqi(String name, String description, Map<String, Double> weights) {
		super(name, description);
		this.weights = (weights == null) ? new HashMap<>() : weights;
	}

	public Tqi(String name, String description, Map<String, Double> weights, Map<String, QualityAspect> qualityAspects) {
		super(name, description);
		this.weights = (weights == null) ? new HashMap<>() : weights;
		this.qualityAspects = qualityAspects;
	}


	// Methods

	/**
	 * Tqi clone needs to work from a bottom-up parse to allow the fully connected
	 * QualityAspect -> ProductFactor layer to pass the cloned property nodes by reference.
	 */
	@Override
	public ModelNode clone() {
		Map<String, QualityAspect> clonedQualityAspects = new HashMap<>();
		Map<String, ProductFactor> clonedProductFactors = new HashMap<>();

		getAnyQualityAspect().getProductFactors().values().forEach(productFactor -> {
			ProductFactor clonedProductFactor = (ProductFactor)productFactor.clone();
			clonedProductFactors.put(clonedProductFactor.getName(), clonedProductFactor);
		});

		getQualityAspects().values().forEach(characteristic -> {
			QualityAspect clonedQualityAspect = (QualityAspect)characteristic.clone(clonedProductFactors);
			clonedQualityAspects.put(clonedQualityAspect.getName(), clonedQualityAspect);
		});

		return new Tqi(getName(), getDescription(), getWeights(), clonedQualityAspects);
	}


	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Tqi)) { return false; }
		Tqi otherTqi = (Tqi) other;

		return getName().equals(otherTqi.getName())
				&& getQualityAspects().size() == otherTqi.getQualityAspects().size();
	}


	/**
	 * @param args
	 * 		Empty args. No parameters needed.
	 */
	@Override
	protected void evaluate(Double... args) {

		if (args.length != 1) throw new RuntimeException("Tqi.evaluate() expects input args of length 1.");

		Double loc = args[0];

		// Assert a weight mapping exists for each provided characteristic
		getWeights().keySet().forEach(k -> {
			if (!getQualityAspects().containsKey(k)) {
				throw new RuntimeException("No weight-measure mapping in root node " + this.getName() +
						"exists for QualityAspect " + k);
			}
		});

		// Evaluate: standard weighted sum of characteristic values
		double sum = 0.0;
		for (Map.Entry<String, Double> weightMap : getWeights().entrySet()) {
			sum += getQualityAspects().get(weightMap.getKey()).getValue(loc) * weightMap.getValue();
		}

		setValue(sum);
	}
}
