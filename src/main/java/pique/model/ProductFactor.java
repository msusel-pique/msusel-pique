package pique.model;

import pique.evaluation.DefaultFactorEvaluator;

public class ProductFactor extends ModelNode {

	// Fields
	private Measure measure;  // TODO (1.0): allow more than one measure

	// Constructors

	public ProductFactor(String name, String description) {
		super(name, description, new DefaultFactorEvaluator(), null);
	}
	
	public ProductFactor(String name, String description, Measure measure){
		super(name, description, new DefaultFactorEvaluator(), null);
		this.measure = measure;
	}



	// Getters and setters

	public Measure getMeasure(){
		return measure;
	}

	public void setMeasure(Measure measure){
		this.measure = measure;
	}

	// Methods

	@Override
	public ModelNode clone() {
		return new ProductFactor(getName(), getDescription(), (Measure)getMeasure().clone());
	}


	@Override
	public boolean equals(Object other) {
		if (!(other instanceof ProductFactor)) { return false; }
		ProductFactor otherProductFactor = (ProductFactor) other;

		return getName().equals(otherProductFactor.getName())
				&& getMeasure().equals(otherProductFactor.getMeasure());
	}

}
