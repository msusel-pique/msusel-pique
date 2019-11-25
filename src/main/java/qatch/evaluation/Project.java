package qatch.evaluation;

import com.google.gson.annotations.Expose;
import qatch.analysis.Diagnostic;
import qatch.model.Property;
import qatch.model.QualityModel;
import qatch.utility.FileUtility;

import java.nio.file.Path;
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
	private QualityModel qualityModel;  // the QM prototype this project uses for evaluation


	// Constructors

	public Project(String name){
		this.name = name;
	}

	public Project(String name, Path path, QualityModel qm) {
		this.name = name;
		this.path = path;
		this.qualityModel = qm.clone();
	}
	
	
	// Getters and setters
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
	public QualityModel getQualityModel() {
		return qualityModel;
	}
	public void setQualityModel(QualityModel qualityModel) {
		this.qualityModel = qualityModel;
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
		for (Property property : getQualityModel().getProperties().values()) {
			for (Diagnostic diagnostic : property.getMeasure().getDiagnostics()) {
				if (diagnostic.getName().equals(toolResult.getName())) { diagnostic.setFindings(toolResult.getFindings()); }
			}
		}
	}

	public void evaluateMeasures() {
		if (getLinesOfCode() < 1) {
			throw new RuntimeException("Normalization of measures failed" +
					". This is likely due to the LoC analyzer either \nfailing to get the " +
					"total lines of code, or failing to assign the TLOC to the project.");
		}

		getQualityModel().getMeasures().values().forEach(m -> {
			m.getValue((double) getLinesOfCode());
		});
	}

	/**
	 * Evaluate and set this project's characteristics using the weights
	 * provided by the quality model and the values contained in the project's Property nodes.
	 */
	public void evaluateCharacteristics() {
		getQualityModel().getCharacteristics().values().forEach(characteristic -> characteristic.getValue((double)getLinesOfCode()));
	}

	/**
	 * Evaluate and set this project's properties using the thresholds
	 * provided by the quality model and the findings contained in the Measure nodes.
	 */
	public void evaluateProperties() {
		getQualityModel().getProperties().values().forEach(property -> property.getValue((double)getLinesOfCode()));
	}

	public void evaluateTqi() {
		getQualityModel().getTqi().getValue((double)getLinesOfCode());
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
	 * Find the diagnostic objects in this project and update their findings with findings containing in
	 * the input parameter.  This method matches the diagnostic objects by name.
	 *
	 * @param diagnosticsWithFindings
	 * 		A map {Key: diagnostic name, Value: diagnostic object} of diagnostics contaning > 0 values of
	 * 		findings.
	 */
	public void updateDiagnosticsWithFindings(Map<String, Diagnostic> diagnosticsWithFindings) {
		diagnosticsWithFindings.values().forEach(diagnostic -> {
			getQualityModel().getMeasures().values().forEach(measure -> {
				measure.getDiagnostics().forEach(oldDiagnostic -> {
					if (oldDiagnostic.getName().equals(diagnostic.getName())) {
						oldDiagnostic.setFindings(diagnostic.getFindings());
					}
				});
			});
		});
	}
}
