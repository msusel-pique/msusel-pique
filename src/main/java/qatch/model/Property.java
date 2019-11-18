package qatch.model;

import com.google.gson.annotations.Expose;
import qatch.analysis.Measure;

public class Property {
	
	// Statics
	public static final int THRESHOLDS_NUM = 3;

	// Fields
	private String description;//A brief description of the property (optional)
	@Expose
	private double value;//The quality score of the property
	@Expose
	private Measure measure;
	@Expose
	private String name;//The name of the property
	@Expose
	private boolean positive;//If this field is true then the metric has a positive impact on the property
	@Expose
	private Double[] thresholds;//The three thresholds of the property metric, needed for the evaluation


	public Property(){
		// Just create the thresholds array.
		thresholds = new Double[THRESHOLDS_NUM];
		measure = new Measure();
	}
	
	public Property(Measure measure){
		thresholds = new Double[THRESHOLDS_NUM];
		this.measure = measure;
	}

	public Property(String name, Measure measure) {
		thresholds = new Double[THRESHOLDS_NUM];
		this.name = name;
		this.measure = measure;
	}

	public Property(String name, String description, boolean impact, Double[] thresholds, Measure measure) {
		this.name = name;
		this.description = description;
		this.positive = impact;
		this.thresholds = thresholds;
		this.measure = measure;
	}
	

	public boolean isPositive() {
		return positive;
	}

	public void setPositive(boolean positive) {
		this.positive = positive;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Double[] getThresholds() {
		return thresholds;
	}
	
	public void setThresholds(Double[] thresholds) {
		this.thresholds = thresholds;
	}
	
	public double getValue() {
		return value;
	}
	
	public void setValue(double value) {
		this.value = value;
	}
	
	public void setMeasure(Measure measure){
		this.measure = measure;
	}
	
	public Measure getMeasure(){
		return measure;
	}

	
	/**
	 * A method for evaluating a Property object (i.e this property). 
	 * In other words, this method calculates the eval field
	 * of a property based on the values of the following fields:
	 * 
	 * 		- positive   : User defined or received by the QM.xml
	 * 		- normValue  : Calculated by the aggregator.
	 * 		- thresholds : Imported by ThresholdImporter or by the
	 * 					   QM.xml file.
	 * 
	 * Typically, it simulates the Utility Function that is used
	 * for the evaluation of the properties.
	 */
	public void evaluate() {
		/*
		 * Check the sign of the impact that this property has on the total quality
		 * and choose the monotony of the utility function.
		 */
		if(positive){
			//If the metric has a positive impact on quality -> Ascending utility function
			if(this.measure.getNormalizedValue() <= this.thresholds[0]){
				//Lower Group
				this.value = 0;
			}else if(this.measure.getNormalizedValue() <= this.thresholds[1]){
				//Middle Group
				this.value = (0.5/(thresholds[1]-thresholds[0]))*(this.getMeasure().getNormalizedValue() - thresholds[0]);
			}else if(this.measure.getNormalizedValue() <= this.thresholds[2]){
				//Upper Group
				this.value = 1 - (0.5/(thresholds[2]-thresholds[1]))*(thresholds[2] - this.getMeasure().getNormalizedValue());
			}else{
				//Saturation
				this.value = 1;
			}
		}else{
			//If the metric has a negative impact on quality -> Descending utility function
			if(roundDown4(this.measure.getNormalizedValue()) <= this.thresholds[0]){
				//Lower Group
				this.value = 1;
			}else if(roundDown4(this.measure.getNormalizedValue()) <= this.thresholds[1]){
				//Middle Group
				this.value = 1 - (0.5/(thresholds[1]-thresholds[0]))*(this.getMeasure().getNormalizedValue() - thresholds[0]);
			}else if(roundDown4(this.measure.getNormalizedValue()) <= this.thresholds[2]){
				//Upper Group
				this.value = (0.5/(thresholds[2]-thresholds[1]))*(thresholds[2] - roundDown4(this.getMeasure().getNormalizedValue()));
			}else{
				//Saturation
				this.value = 0;
			}
		}
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
