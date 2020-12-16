package pique.model;

import pique.evaluation.*;

import java.util.HashMap;
import java.util.Map;

public class ProductFactor extends ModelNode {

	// Constructors

	public ProductFactor(String name, String description) {
		super(name, description, new DefaultFactorEvaluator(), new DefaultNormalizer());
	}

	public ProductFactor(String name, String description, IEvaluator evaluator, INormalizer normalizer,
						 IUtilityFunction utilityFunction, Map<String, Double> weights, Double[] thresholds) {
		super(name, description, evaluator, normalizer, utilityFunction, weights, thresholds);
	}

	public ProductFactor(String name, String description, IEvaluator evaluator) {
		super(name, description, new DefaultFactorEvaluator(), new DefaultNormalizer());
		this.evaluatorObject = evaluator;
	}
	
	public ProductFactor(String name, String description, ModelNode measure){
		super(name, description, new DefaultFactorEvaluator(), new DefaultNormalizer());
		this.children.put(measure.getName(), measure);
	}

	// Used for cloning
	public ProductFactor(double value, String name, String description, IEvaluator evaluator, INormalizer normalizer,
						 IUtilityFunction utilityFunction, Map<String, Double> weights, Double[] thresholds, Map<String,
			ModelNode> children) {
		super(value, name, description, evaluator, normalizer, utilityFunction, weights, thresholds, children);
	}



	//region Methods

	@Override
	public ModelNode clone() {

		Map<String, ModelNode> clonedChildren = new HashMap<>();
		getChildren().forEach((k, v) -> clonedChildren.put(k, v.clone()));

		return new ProductFactor(getValue(), getName(), getDescription(), getEvaluatorObject(), getNormalizerObject(),
				getUtilityFunctionObject(), getWeights(), getThresholds(), clonedChildren);
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof ProductFactor)) { return false; }
		ProductFactor otherProductFactor = (ProductFactor) other;

		return getName().equals(otherProductFactor.getName())
				&& getAnyChild().equals(otherProductFactor.getAnyChild());
	}

	//endregion

}
