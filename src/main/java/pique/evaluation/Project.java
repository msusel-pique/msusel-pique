package pique.evaluation;

import com.google.gson.annotations.Expose;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import org.apache.commons.lang3.tuple.Pair;
import pique.model.*;

import java.nio.file.Path;
import java.util.Map;

/**
 * This class represents a project under evaluation.
 */

public class Project{

	// Fields

	private String name;
	private int linesOfCode;
	private Path path;  // the original path where the sources of the project are stored (with or without the name)
	private QualityModel qualityModel;  // the QM prototype this project uses for evaluation

	// Constructors

	public Project(String name){
		this.name = name;
	}

	public Project(String name, QualityModel qm) {
		this.name = name;
		this.qualityModel = qm.clone();
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
	// TODO (1.0): Currently makes assumption that all product factors have exactly 1 connected measure
	public void addFindings(Diagnostic toolResult) {

		for (ModelNode measure : getQualityModel().getMeasures().values()) {
			for (ModelNode diagnostic : measure.getChildren().values()) {
				if (diagnostic.getName().equals(toolResult.getName())) {
					diagnostic.setChildren(toolResult.getChildren().values());
				}
			}
		}
	}

	public void evaluateMeasures() {
		getQualityModel().getMeasures().values().forEach(ModelNode::getValue);
	}

	/**
	 * Evaluate and set this project's characteristics using the weights
	 * provided by the quality model and the values contained in the project's ProductFactor nodes.
	 */
	public void evaluateCharacteristics() {
		getQualityModel().getQualityAspects().values().forEach(ModelNode::getValue);
	}


	/**
	 * Evaluate and set this project's properties using the thresholds
	 * provided by the quality model and the findings contained in the Measure nodes.
	 */
	public void evaluateProperties() {
		getQualityModel().getProductFactors().values().forEach(ModelNode::getValue);
	}


	public void evaluateTqi() {
		getQualityModel().getTqi().getValue();
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
		Pair<String, String> loc = Pair.of("projectLinesOfCode", String.valueOf(getLinesOfCode()));
		Pair<String, String> name = Pair.of("projectName", getName());

		QualityModelExport qmExport = new QualityModelExport(getQualityModel(), loc, name);
		return qmExport.exportToJson(fileName, resultsDir);
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
				measure.getChildren().values().forEach(oldDiagnostic -> {
					if (oldDiagnostic.getName().equals(diagnostic.getName())) {
						oldDiagnostic.setChildren(diagnostic.getChildren());
					}
				});
			});
		});
	}
}
