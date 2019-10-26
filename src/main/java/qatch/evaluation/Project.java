package qatch.evaluation;

import qatch.analysis.Measure;
import qatch.model.*;

import java.util.*;

/**
 * This class represents a project under evaluation.
 * @author Miltos
 */
public class Project{
	
	// instance variables
	private int linesOfCode;
	private String name;
	private String path; //The original path where the sources of the project are stored (with or without the name)
	private Map<String, Measure> measures = new HashMap<>();
	private MetricSet metrics;
	private PropertySet properties_depreicated;
	private CharacteristicSet characteristics_depreicated;
	private Tqi tqi;

	
	/*
	 * The constructor methods of this class.
	 */
	public Project(){
		this.metrics = new MetricSet();
		this.properties_depreicated = new PropertySet();
		this.characteristics_depreicated = new CharacteristicSet();
		this.tqi = new Tqi();
	}
	
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

	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}

	public Map<String, Measure> getMeasures() {
		return measures;
	}
	public void setMeasures(Map<String, Measure> measures) {
		this.measures = measures;
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

	public Tqi getTqi() {
		return tqi;
	}
	public void setTqi(Tqi tqi) {
		this.tqi = tqi;
	}

	//Other
	/**
	 * Adds a Metrics object in the metrics vector.
	 */
	public void addMetrics(Metrics m){
		this.metrics.addMetrics(m);
	}
	
	/**
	 * Return the Metrics Object placed in the index 
	 * position of the MetricSet.
	 */
	public Metrics getMetrics(int index){
		return this.metrics.get(index);
	}
	
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

	/**
	 * Clone the properties of the quality model to the properties of the certain project
	 */
	public void cloneProperties(QualityModel qualityModel) {
		for(int i = 0; i < qualityModel.getProperties_deprecated().size(); i++){
			//Clone the property and add it to the PropertySet of the current project
			Property p = null;
			try { p = (Property) qualityModel.getProperties_deprecated().get(i).clone(); }
			catch (CloneNotSupportedException e) { e.printStackTrace(); }

			this.addProperty(p);
		}
	}

}
