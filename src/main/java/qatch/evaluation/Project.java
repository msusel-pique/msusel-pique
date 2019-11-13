package qatch.evaluation;

import com.google.gson.annotations.Expose;
import qatch.analysis.Diagnostic;
import qatch.analysis.Measure;
import qatch.model.Characteristic;
import qatch.model.Property;
import qatch.model.QualityModel;
import qatch.model.Tqi;
import qatch.utility.FileUtility;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a project under evaluation.
 * @author Miltos, Rice
 */

public class Project{

	// Fields

	@Expose
	private String name;
	@Expose
	private int linesOfCode;
	private Path path;  // the original path where the sources of the project are stored (with or without the name)
	@Expose
	private Map<String, Characteristic> characteristics = new HashMap<>();
	@Expose
	private Map<String, Property> properties = new HashMap<>();  // each property has one Measure associated with it
	@Expose
	private Tqi tqi;


	// Constructors

	public Project(String name){
		this.name = name;
		this.tqi = new Tqi(null, null, null);
	}

	public Project(String name, Path path, QualityModel qm) {
		this.name = name;
		this.path = path;
		initializeTqi(qm);
		initializeCharacteristics(qm);
		initializeProperties(qm);
	}
	
	
	// Getters and setters
	public void setDiagnostics(Map<String, Diagnostic> diagnostics) {
		diagnostics.values().forEach(diagnostic -> {
			getMeasures().values().forEach(measure -> {
				measure.getDiagnostics().forEach(oldDiagnostic -> {
					if (oldDiagnostic.getId().equals(diagnostic.getId())) {
						oldDiagnostic = diagnostic;
					}
				});
			});
		});
	}
	public Characteristic getCharacteristic(String name) { return this.characteristics.get(name); }
	public Map<String, Characteristic> getCharacteristics() {
		return getTqi().getCharacteristics();
	}
	public void setCharacteristic(String name, Characteristic characteristic) { this.characteristics.put(name, characteristic); }
	public void setCharacteristics(Map<String, Characteristic> characteristics) {
		this.characteristics = characteristics;
	}
	public int getLinesOfCode() { return linesOfCode; }
	public void setLinesOfCode(int linesOfCode) { this.linesOfCode = linesOfCode; }
	public Measure getMeasure(String measureName) {
		return getMeasures().get(measureName);
	}
	public Measure getMeasureByPropertyName(String propertyName) {
		return this.properties.get(propertyName).getMeasure();
	}
	public Map<String, Measure> getMeasures() {
		Map<String, Measure> measures = new HashMap<>();
		this.getProperties().values().forEach(p -> { measures.put(p.getMeasure().getName(), p.getMeasure()); });
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Path getPath() { return path; }
	public void setPath(Path path) { this.path = path; }
	public Property getProperty(String name) { return this.properties.get(name); }
	public Map<String, Property> getProperties() { return this.properties; }
	public void setProperty(String name, Property property) {
		this.properties.put(name, property);
	}
	public Tqi getTqi() {
		return this.tqi;
	}
	public void setTqi(Tqi tqi) {
		this.tqi = tqi;
	}


	// Methods

	/**
	 * Search through diagnostics associated with this project and update the findings of the diagnostic
	 * with the diagnostic resulting from a tool analysis run.
	 *
	 * Search time complexity is O(n) where n is number of diagnostics in the project.
	 *
	 * @param toolResult
	 * 		A diagnostic object parsed from the tool result file
	 */
	public void addFindings(Diagnostic toolResult) {
		for (Property property : this.properties.values()) {
			for (Diagnostic diagnostic : property.getMeasure().getDiagnostics()) {
				if (diagnostic.getId().equals(toolResult.getId())) { diagnostic.setFindings(toolResult.getFindings()); }
			}
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
			m.setNormalizedValue(m.getValue() / (double) this.getLinesOfCode());
		});
	}

	/**
	 * Evaluate and set this project's characteristics using the weights
	 * provided by the quality model and the values contained in the project's Property nodes.
	 */
	public void evaluateCharacteristics() {
		this.getCharacteristics().values().forEach(Characteristic::evaluate);
	}

	/**
	 * Evaluate and set this project's properties using the thresholds
	 * provided by the quality model and the findings contained in the Measure nodes.
	 */
	public void evaluateProperties() {
		getProperties().values().forEach(Property::evaluate);
	}

	public void evaluateTqi() {
		getTqi().evaluate();
	}

	/**
	 * Create a hard-drive file representation of the project.
	 *
	 * @param resultsDir
	 * 		The directory to place the project representation file into.  Does not need to exist beforehand.
	 * @return
	 * 		The path of the project json file.
	 */
	public Path exportToJson(Path resultsDir) {

		String fileName = this.getName() + "_evalResults";
		return FileUtility.exportObjectToJson(this, resultsDir, fileName);
	}

	/**
	 * Initialize characteristics layer of the project using the quality model description
	 *
	 * @param qm
	 * 		The language-specific quality model
	 */
	private void initializeCharacteristics(QualityModel qm) {
		this.characteristics = qm.getCharacteristics();
	}

	/**
	 * Initialize properties layer of the project using the quality model description
	 *
	 * @param qm
	 * 		The language-specific quality model
	 */
	private void initializeProperties(QualityModel qm) {
		this.properties = qm.getProperties();
	}

	/**
	 * Initialize Tqi object of the project using weights from the quality model
	 *
	 * @param qm
	 * 		The language-specific quality model
	 */
	private void initializeTqi(QualityModel qm) {
		this.tqi = qm.getTqi();
		this.tqi.setCharacteristics(qm.getCharacteristics());

	}
}
