package qatch.evaluation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import qatch.analysis.Measure;
import qatch.model.Characteristic;
import qatch.model.Property;
import qatch.model.QualityModel;
import qatch.model.Tqi;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents a project under evaluation.
 * @author Miltos, Rice
 */

public class Project{

	// instance variables
	@Expose
	private int linesOfCode;
	@Expose
	private String name;
	private Path path; //The original path where the sources of the project are stored (with or without the name)
	@Expose
	private Map<String, Characteristic> characteristics = new HashMap<>();
	@Expose
	private Map<String, Property> properties = new HashMap<>();  // each property has one Measure associated with it
	@Expose
	private Tqi tqi;

	
	// Constructors
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
	
	
	// Getters and setters.
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

	public Measure getMeasure(String propertyName) {
		return this.properties.get(propertyName).getMeasure();
	}
	public List<Measure> getMeasures() {
		ArrayList<Measure> measures = new ArrayList<>();
		this.getProperties().values().forEach(p -> { measures.add(p.getMeasure()); });
		return measures;
	}
	public void setMeasure(String propertyName, Measure measure) {
		this.getProperty(propertyName).setMeasure(measure);
	}
	public void setMeasures(Map<String, Measure> measures) {
		for (Map.Entry<String, Measure> propertyAndMeasures : measures.entrySet()) {
			String propertyName = propertyAndMeasures.getKey();
			Measure measure = propertyAndMeasures.getValue();
			this.setMeasure(propertyName, measure);
		}
	}

	public Property getProperty(String name) { return this.properties.get(name); }
	public Map<String, Property> getProperties() { return this.properties; }
	public void setProperty(String name, Property property) {
		this.properties.put(name, property);
	}

	public Characteristic getCharacteristic(String name) { return this.characteristics.get(name); }
	public Map<String, Characteristic> getCharacteristics() { return this.characteristics; }
	public void setCharacteristic(String name, Characteristic characteristic) { this.characteristics.put(name, characteristic); }

	public Tqi getTqi() {
		return tqi;
	}
	public void setTqi(Tqi tqi) {
		this.tqi = tqi;
	}


	// Methods
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

		this.getMeasures().forEach(m -> {
			m.setNormalizedValue(m.getValue() / (double) this.getLinesOfCode());
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
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

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
