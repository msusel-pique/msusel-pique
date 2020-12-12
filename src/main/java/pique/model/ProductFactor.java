package pique.model;

import pique.evaluation.DefaultFactorEvaluator;
import pique.evaluation.IEvaluator;
import pique.evaluation.INormalizer;

import java.util.HashMap;
import java.util.Map;

public class ProductFactor extends ModelNode {

	// Fields
	// TODO (1.0): Changing single "Measure" field to new "Children" map. This will likely break a lot of things.
//	private Measure measure;  // TODO (1.0): allow more than one measure

	// Constructors

	public ProductFactor(String name, String description) {
		super(name, description, new DefaultFactorEvaluator(), null);
	}
	
	public ProductFactor(String name, String description, ModelNode measure){
		super(name, description, new DefaultFactorEvaluator(), null);
		this.children.put(measure.getName(), measure);
	}

	public ProductFactor(double value, String name, String description, IEvaluator evaluator, INormalizer normalizer,
				   Map<String, ModelNode> children, Map<String, Double> weights) {
		super(value, name, description, evaluator, normalizer, children, weights);
	}


	//region Methods

	@Override
	public ModelNode clone() {

		Map<String, ModelNode> clonedChildren = new HashMap<>();
		getChildren().forEach((k, v) -> clonedChildren.put(k, v.clone()));

		return new ProductFactor(getValue(), getName(), getDescription(), getEvaluator(), getNormalizer(),
				clonedChildren, getWeights());
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
