package qatch.evaluation;

import qatch.analysis.Measure;
import qatch.model.*;

import java.nio.file.Path;
import java.util.*;

/**
 * This class represents a project under evaluation.
 * @author Miltos, Rice
 */

public class Project{

	// instance variables
	private int linesOfCode;
	private String name;
	private Path path; //The original path where the sources of the project are stored (with or without the name)
	private Map<String, Measure> measures = new HashMap<>();
	private Map<String, Characteristic> characteristics = new HashMap<>();
	private Map<String, Property> properties = new HashMap<>();  // each property has one Measure associated with it
	private Tqi tqi;
	private MetricSet metrics;
	private PropertySet properties_depreicated;
	private CharacteristicSet characteristics_depreicated;

	
	/*
	 * constructors
	 */
	public Project(String name, Path path, QualityModel qm) {
		this.name = name;
		this.path = path;
		initializeProperties(qm);
		initializeCharacteristics(qm);
	}

	@Deprecated
	public Project() {
		this.metrics = new MetricSet();
		this.properties_depreicated = new PropertySet();
		this.characteristics_depreicated = new CharacteristicSet();
		this.tqi = new Tqi();
	}

	@Deprecated
	public Project(String name){
		this.name = name;
		this.metrics = new MetricSet();
		this.properties_depreicated = new PropertySet();
		this.characteristics_depreicated = new CharacteristicSet();
		this.tqi = new Tqi();
	}
	
	
	/*
	 * Getters and setters.
	 */
	public int getLinesOfCode() { return linesOfCode; }
	public void setLinesOfCode(int linesOfCode) { this.linesOfCode = linesOfCode; }

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Path getPath() { return path; }
	public void setPath(Path path) { this.path = path; }

	public Measure getMeasure(String propertyName) { return this.measures.get(propertyName); }
	public Map<String, Measure> getMeasures() {
		return measures;
	}
	public void setMeasures(Map<String, Measure> measures) {
		this.measures = measures;
	}

	public Property getProperty(String name) { return this.properties.get(name); }
	public void setProperty(String name, Property property) {
		this.properties.put(name, property);
	}
	public Map<String, Property> getProperties() { return properties; }

	public Map<String, Characteristic> getCharacteristics() { return characteristics; }
	public void setCharacteristic(String name, Characteristic characteristic) { this.characteristics.put(name, characteristic); }

	public Tqi getTqi() {
		return tqi;
	}
	public void setTqi(Tqi tqi) {
		this.tqi = tqi;
	}

	public PropertySet getProperties_depreicated() {
		return properties_depreicated;
	}
	public void setProperties_depreicated(PropertySet properties_depreicated) {
		this.properties_depreicated = properties_depreicated;
	}
	
	public MetricSet getMetrics() {
		return metrics;
	}
	public void setMetrics(MetricSet metrics) {
		this.metrics = metrics;
	}
	
	public CharacteristicSet getCharacteristics_depreicated() {
		return characteristics_depreicated;
	}
	public void setCharacteristics_depreicated(CharacteristicSet characteristics_depreicated) {
		this.characteristics_depreicated = characteristics_depreicated;
	}




	// methods
	/**
	 * Attach measures to this project's Properties layers.
	 * This function provides many checks for pre and post conditions of the
	 * model's expected state as it relates to the quality model and tool evaluation
	 */
	public void applyMeasures() {

		// assert project measures (from tool analysis) have been created
		if (this.getMeasures().isEmpty()) {
			throw new RuntimeException("Measure objects from Tool analysis run must exist before apply measures");
		}

		for (String propertyName : this.getProperties().keySet()) {
			// assert a measure mapping exists for each property
			if (!this.getMeasures().containsKey(propertyName)) {
				throw new RuntimeException("No measure mapping was found for property: " + propertyName);
			}
			// add measure to Property object
			this.getProperty(propertyName).setMeasure(this.getMeasure(propertyName));
		}
	}

	/**
	 * Evaluate and set the this project's Measures.normalizedValue values according using the lines
	 * of code value in the project
	 */
	public void evaluateMeasures() {

		if (this.linesOfCode < 1) {
			throw new RuntimeException("Normalization of measures failed" +
					". This is likely due to the LoC analyzer either \nfailing to get the " +
					"total lines of code, or failing to assign the TLOC to the project.");
		}
		this.getMeasures().values().forEach(m -> {
			m.setNormalizedValue(m.getValue() / (double) this.linesOfCode);
		});
	}

	/**
	 * Evaluate and set this project's characteristics using the weights
	 * provided by the quality model and the values contained in the project's Property nodes.
	 */
	public void evaluateCharacteristics() {
		this.getCharacteristics().values().forEach(c -> { c.evaluate(this.getProperties()); });
	}

	/**
	 * Evaluate and set this project's properties using the thresholds
	 * provided by the quality model and the findings contained in the Measure nodes.
	 */
	public void evaluateProperties() {
		this.getProperties().values().forEach(Property::evaluate);
	}

	/**
	 * Initialize characteristics layer of the project using the quality model description
	 *
	 * @param qm
	 * 		The language-specific quality model
	 */
	private void initializeCharacteristics(QualityModel qm) {
		for (String characteristicName : qm.getCharacteristics().keySet()) {

			// create new project characteristic entry using weights information from quality model
			Characteristic reference = qm.getCharacteristic(characteristicName);
			Characteristic projectCharacteristic = new Characteristic(
					characteristicName, reference.getStandard(),
					reference.getDescription(), reference.getWeights()
			);
			this.setCharacteristic(characteristicName, projectCharacteristic);
		}
	}

	/**
	 * Initialize properties layer of the project using the quality model description
	 *
	 * @param qm
	 * 		The language-specific quality model
	 */
	private void initializeProperties(QualityModel qm) {
		for (String propertyName : qm.getProperties().keySet()) {

			// create new project property entry using thresholds and impact from quality model
			Property referenceProperty = qm.getProperty(propertyName);
			Property projectProperty = new Property(
					propertyName, referenceProperty.getDescription(), referenceProperty.isPositive(),
					referenceProperty.getThresholds(), null
			);
			this.setProperty(propertyName, projectProperty);
		}
	}



	/**
	 * Adds a Metrics object in the metrics vector.
	 */
	@Deprecated
	public void addMetrics(Metrics m){
		this.metrics.addMetrics(m);
	}

	@Deprecated
	public void addProperty(Property property){
		this.properties_depreicated.getPropertyVector().add(property);
	}
	
	/**
	 * A method that calculates the total evaluation (TQI) of 
	 * this Project.
	 */
	public void calculateTQI(){
		/*
		 * Just call the calculateTQI() method of the Tqi object in order to
		 * calculate the value of the TQI from the eval field of the model's
		 * characteristics
		 */
		this.tqi.calculateTQI(this.characteristics_depreicated);
	}

	/**
	 * This method is responsible for sorting a group of projects according to a desired
	 * field in a descending order. Typically, it is used in order to sort the projects
	 * under evaluation according to their TQI.
     */
	@Deprecated
	public static void sort(final String field, Vector<Project> projects) {
	    Collections.sort(projects, new Comparator<Project>() {
	        @Override
	        public int compare(Project p1, Project p2) {
	            if(field.equals("eval")) {
	                return Double.compare(p1.getTqi().getEval(), p2.getTqi().getEval());
	            }else{
	            	return 1;
	            }
	        }           
	    });
	}
}
