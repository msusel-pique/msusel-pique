package qatch.model;

import com.google.gson.annotations.Expose;
import qatch.analysis.Measure;

public class Property extends ModelNode {
	
	// Statics

	private static final int THRESHOLDS_NUM = 3;

	// Fields

	@Expose
	private Measure measure;
	@Expose
	private boolean positive;//If this field is true then the metric has a positive impact on the property
	@Expose
	private Double[] thresholds;//The three thresholds of the property metric, needed for the evaluation


	// Constructors

	public Property(String name, String description) {
		super(name, description);
		thresholds = new Double[THRESHOLDS_NUM];
	}

	public Property(){
		// Just create the thresholds array.
		super(null, null);
		thresholds = new Double[THRESHOLDS_NUM];
		measure = new Measure();
	}
	
	public Property(String name, String description, Measure measure){
		super(name, description);
		thresholds = new Double[THRESHOLDS_NUM];
		this.measure = measure;
	}


	public Property(String name, String description, boolean impact, Double[] thresholds, Measure measure) {
		super(name, description);
		this.positive = impact;
		this.thresholds = thresholds;
		this.measure = measure;
	}


	// Getters and setters

	public boolean isPositive() {
		return positive;
	}
	public void setPositive(boolean positive) {
		this.positive = positive;
	}
	public Double[] getThresholds() {
		return thresholds;
	}
	public void setThresholds(Double[] thresholds) {
		this.thresholds = thresholds;
	}
	public void setMeasure(Measure measure){
		this.measure = measure;
	}
	public Measure getMeasure(){
		return measure;
	}


	// Methods

	@Override
	public ModelNode clone() {
		return new Property(getName(), getDescription(), isPositive(), getThresholds(), (Measure)getMeasure().clone());
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Property)) { return false; }
		Property otherProperty = (Property) other;

		return getName().equals(otherProperty.getName())
				&& getMeasure().equals(otherProperty.getMeasure());
	}

	/**
	 * A method for evaluating a Property object (i.e this property). In other words, this method calculates the eval
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
	 *      Empty args. No parameters needed.
	 */
	@Override
	protected void evaluate(Double... args) {

		if (args.length == 0) {
			/*
			 * Check the sign of the impact that this property has on the total quality
			 * and choose the monotony of the utility function.
			 */
			Double measureValue = getMeasure().getValue();
			if (positive) {
				//If the metric has a positive impact on quality -> Ascending utility function
				if (measureValue <= this.thresholds[0]) {
					//Lower Group
					setValue(0);
				} else if (measureValue <= this.thresholds[1]) {
					//Middle Group
					setValue((0.5 / (thresholds[1] - thresholds[0])) * (measureValue - thresholds[0]));
				} else if (measureValue <= this.thresholds[2]) {
					//Upper Group
					setValue(1 - (0.5 / (thresholds[2] - thresholds[1])) * (thresholds[2] - measureValue));
				} else {
					//Saturation
					setValue(1);
				}
			} else {
				//If the metric has a negative impact on quality -> Descending utility function
				if (roundDown4(measureValue) <= this.thresholds[0]) {
					//Lower Group
					setValue(1);
				} else if (roundDown4(measureValue) <= this.thresholds[1]) {
					//Middle Group
					setValue(1 - (0.5 / (thresholds[1] - thresholds[0])) * (measureValue - thresholds[0]));
				} else if (roundDown4(measureValue) <= this.thresholds[2]) {
					//Upper Group
					setValue((0.5 / (thresholds[2] - thresholds[1])) * (thresholds[2] - roundDown4(measureValue)));
				} else {
					//Saturation
					setValue(0);
				}
			}
		}

		else throw new RuntimeException("Property.evaluate() expects input args of length 0.");
	}
	
	/**
	 * A method for keeping only the first four digits
	 * of a double number.
	 * 
	 * Used as a quick fix for precision difference spotted
	 * between the numbers calculated by Java and those received
	 * from R Analysis.
	 */
	//TODO: Find a better way of fixing this issue (i.e. increase the precision in R)
	private static double roundDown4(double d) {
	    return (long) (d * 1e4) / 1e4;
	}

}
