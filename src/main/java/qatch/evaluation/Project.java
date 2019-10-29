package qatch.evaluation;

import com.google.gson.Gson;
import qatch.analysis.Measure;
import qatch.model.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

	
	/*
	 * constructors
	 */
	public Project(String name){
		this.name = name;
		this.tqi = new Tqi(null, null);
	}

	public Project(String name, Path path, QualityModel qm) {
		this.name = name;
		this.path = path;
		initializeProperties(qm);
		initializeCharacteristics(qm);
		this.tqi = initializeTqi(qm);
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

	public Map<String, Characteristic> getCharacteristics() { return this.characteristics; }
	public void setCharacteristic(String name, Characteristic characteristic) { this.characteristics.put(name, characteristic); }

	public Tqi getTqi() {
		return tqi;
	}
	public void setTqi(Tqi tqi) {
		this.tqi = tqi;
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

	public void evaluateTqi() {
		this.getTqi().evaluate(this.getCharacteristics());
	}

	/**
	 * Create a hard-drive file representation of the project evaluation results
	 * @return
	 * 		The path to the hard-drive evaluation file
	 *
	 */
	public Path exportEvaluation(Path resultsDir) {

		// ensure target path directory exists
		resultsDir.toFile().mkdirs();

		// instantiate results file reference
		File evalResults = new File(resultsDir.toFile(), this.getName() + "_evalResults.json");

		//Instantiate a Json Parser
		Gson gson = new Gson();

		//Create the Json String of the projects
		String json = gson.toJson(this);

		//Save the results
		try {
			FileWriter writer = new FileWriter(evalResults.toString());
			writer.write(json);
			writer.close();
		} catch(IOException e){
			System.out.println(e.getMessage());
		}

		return evalResults.toPath();
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
	 * Initialize Tqi object of the project using weights from the quality model
	 *
	 * @param qm
	 * 		The language-specific quality model
	 */
	private Tqi initializeTqi(QualityModel qm) {
		String name = qm.getTqi().getName();
		Map<String, Double> weights = qm.getTqi().getWeights();
		return new Tqi(name, weights);
	}
}
