package pique.model;

public class ProductFactor extends ModelNode {

	// Fields
	private Measure measure;  // (TODO): allow more than one measure

	// Constructors

	public ProductFactor(String name, String description) {
		super(name, description);
	}
	
	public ProductFactor(String name, String description, Measure measure){
		super(name, description);
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


	/**
	 * A method for evaluating a ProductFactor object (i.e this property). In other words, this method calculates the eval
	 * field of a property based on the values of the following fields:
	 * 
	 * 		- positive   : User defined or received by the QM.xml
	 * 		- normValue  : Calculated by the aggregator.
	 * 		- thresholds : Imported by ThresholdImporter or by the
	 * 					   QM.xml file.
	 * 
	 * Typically, it simulates the Utility Function that is used  for the evaluation of the properties.
	 *
	 * @param args
	 *      A single entry representing the lines of code. This is needed for normalization at the measure level.
	 */
	@Override
	protected void evaluate(Double... args) {

		if (args.length != 1) throw new RuntimeException("ProductFactor.evaluate() expects input args of lenght 1.");

		// (TODO): multiple measure weighted sums evaluation
		Double loc = args[0];
		setValue(getMeasure().getValue(loc));
	}

}
