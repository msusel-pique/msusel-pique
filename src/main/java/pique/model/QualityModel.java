package pique.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import pique.calibration.*;
import pique.evaluation.DefaultDiagnosticEvaluator;
import pique.evaluation.IEvaluator;
import pique.evaluation.INormalizer;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;

/**
 * This class encapsulates all the appropriate information that describe a 
 * Quality Model that can be used for the evaluation of a single project or 
 * a benchmark of projects.
 * 
 * Typically, it is used in order to load the PropertySet and the CharacteristicSet
 * of the XML file that describes the quality model and assign their values to the 
 * project (or projects) that we want to evaluate.
 * 
 * @author Miltos, Rice
 */
/*
 * TODO:
 *   (1) Add checks immediately after model creation that no duplicate node names by-level exist
 */
public class QualityModel {

	// Fields
	@Expose
	private String name;  //The name of the QM found in the XML file
	@Expose
	private Tqi tqi;  // root node, the total quality evaluation, contains characteristic objects as children
	private IBenchmarker benchmarker;
	private IWeighter weighter;


	// Constructors

	/**
	 * Constructor for deriving QM object from a disk file description (likely .json or .xml)
	 * @param qmFilePath
	 * 		File path to the quality model description
	 */
	public QualityModel(Path qmFilePath) {
		importQualityModel(qmFilePath);
	}

	/**
	 * Constructor for use in deep cloning this quality model
	 *
	 * @param name
	 * 		Quality model name
	 * @param tqi
	 * 		Root node of quality model tree. Careful passing this by reference.
	 * 		Will likely want to use this.tqi.clone().
	 */
	public QualityModel(String name, Tqi tqi, IBenchmarker benchmarker, IWeighter weighter) {
		this.name = name;
		this.tqi = tqi;
		this.benchmarker = benchmarker;
		this.weighter = weighter;
	}


	// Getters and Setters
	public IBenchmarker getBenchmarker() {
		return benchmarker;
	}

	public QualityAspect getAnyQualityAspect() {
		return getTqi().getAnyQualityAspect();
	}
	public QualityAspect getQualityAspect(String name) {
		return getTqi().getQualityAspects().get(name);
	}
	public Map<String, QualityAspect> getQualityAspects() {
		return getTqi().getQualityAspects();
	}
	public void setQualityAspect(String qualityAspectName, QualityAspect qualityAspect) {
		getTqi().getQualityAspects().put(qualityAspectName, qualityAspect);
	}
	public void setQualityAspects(Map<String, QualityAspect> qualityAspect) {
		getTqi().setQualityAspects(qualityAspect);
	}

	public void setFinding(Measure diagnosticsMeasure, String diagnosticName, Finding finding) {
		diagnosticsMeasure.getDiagnostic(diagnosticName).setFinding(finding);
	}

	public Measure getMeasure(String measureName) {
		for (ProductFactor productFactor : getProductFactors().values()) {
			if (productFactor.getMeasure().getName().equals(measureName)) {
				return productFactor.getMeasure();
			}
		}
		throw new RuntimeException("Unable to find measure with name " + measureName + " from QM's property nodes");
	}
	public Map<String, Measure> getMeasures() {
		Map<String, Measure> measures = new HashMap<>();
		List<ProductFactor> productFactorList = new ArrayList<>(getProductFactors().values());
		productFactorList.forEach(productFactor -> {
			measures.put(productFactor.getMeasure().getName(), productFactor.getMeasure());
		});
		return measures;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public ProductFactor getProductFactor(String name) {
		return getProductFactors().get(name);
	}
	public ProductFactor getProductFactorByMeasureName(String measureName) {
		for (ProductFactor productFactor : getProductFactors().values()) {
			if (productFactor.getMeasure().getName().equals(measureName)) {
				return productFactor;
			}
		}
		throw new RuntimeException("Unable to find property with child measure name " + measureName + " from QM's property nodes");
	}
	public Map<String, ProductFactor> getProductFactors() {
		return getAnyQualityAspect().getProductFactors();
	}
	public void setProductFactor(ProductFactor productFactor) {
		Set<QualityAspect> allQualityAspects = new HashSet<>(getTqi().getQualityAspects().values());
		allQualityAspects.forEach(qualityAspect -> {
			qualityAspect.setProductFactor(productFactor);
		});
	}
	public void setProductFactors(Map<String, ProductFactor> productFactors) {
		Set<QualityAspect> allQualityAspects = new HashSet<>(getTqi().getQualityAspects().values());
		allQualityAspects.forEach(qualityAspect -> {
			qualityAspect.setProductFactors(productFactors);
		});
	}

	public Tqi getTqi() {
		return tqi;
	}
	public void setTqi(Tqi tqi) {
		this.tqi = tqi;
	}

	public IWeighter getWeighter() {
		return weighter;
	}
	private void setWeighter(IWeighter weighter) {
		this.weighter = weighter;
	}

	// Methods
	/**
	 * @return
	 * 		Deep clone of this QualityModel object
	 */
	@Override
	public QualityModel clone() {
		Tqi rootNode = (Tqi)getTqi().clone();
		return new QualityModel(getName(), rootNode, getBenchmarker(), getWeighter());
	}


	@Override
	public boolean equals(Object other) {
		if (!(other instanceof QualityModel)) { return false; }
		QualityModel otherQm = (QualityModel) other;

		return getName().equals(otherQm.getName())
				&& getTqi().equals(otherQm.getTqi());
	}

	/**
	 * Create a hard-drive file representation of the model
	 *
	 * @param outputDirectory
	 * 		The directory to place the QM file into.  Does not need to exist beforehand.
	 * @return
	 * 		The path of the exported model file.
	 */
	public Path exportToJson(Path outputDirectory) {
		String fileName = "qualityModel_" + getName().replaceAll("\\s","");
		QualityModelExport qmExport = new QualityModelExport(this);
		return qmExport.exportToJson(fileName, outputDirectory);
	}


	/**
	 * This method is responsible for importing the desired Quality Model
	 * by parsing the file that contains the text data of the Quality Model.
	 */
	private void importQualityModel(Path qmFilePath) {

		// TODO: assert well-formed quality model json file

		// Parse json data and update quality model object
		try {
			// TODO: break this large method into smaller method calls
			FileReader fr = new FileReader(qmFilePath.toString());
			JsonObject jsonQm = new JsonParser().parse(fr).getAsJsonObject();
			fr.close();

			// Name
			setName(jsonQm.getAsJsonPrimitive("name").getAsString());

			// Optional benchmark strategy
			if (jsonQm.getAsJsonObject("global_config") != null && jsonQm.getAsJsonObject("global_config").get("benchmark_strategy") != null) {
				String fullClassName = jsonQm.getAsJsonObject("global_config").get("benchmark_strategy").getAsString();
				benchmarker = (IBenchmarker) Class.forName(fullClassName).getConstructor().newInstance();
			}
			else {
				benchmarker = new NaiveBenchmarker();
			}

			// Optional weighter strategy
			if (jsonQm.getAsJsonObject("global_config") != null && jsonQm.getAsJsonObject("global_config").get("weights_strategy") != null) {
				String fullClassName = jsonQm.getAsJsonObject("global_config").get("weights_strategy").getAsString();
				weighter = (IWeighter) Class.forName(fullClassName).getConstructor().newInstance();
			}
			else {
				weighter = new NaiveWeighter();
			}

			// Factors and measures collections as json
			JsonObject factors = jsonQm.getAsJsonObject("factors");
			JsonObject tqi = factors.getAsJsonObject("tqi");
			JsonObject qualityAspects = factors.getAsJsonObject("quality_aspects");
			JsonObject productFactors = factors.getAsJsonObject("product_factors");
			JsonObject measures = jsonQm.getAsJsonObject("measures");

			// TQI (instance objects first, then add factor weights passing back up the DOM)
			Entry<String, JsonElement> tqiEntry = tqi.entrySet().iterator().next();
			String tqiName = tqiEntry.getKey();
			String tqiDescription = tqiEntry.getValue().getAsJsonObject().get("description").getAsString();
			setTqi(new Tqi(tqiName, tqiDescription, null));

			// Quality Aspects
			qualityAspects.entrySet().forEach(entry -> {
				JsonObject valueObj = entry.getValue().getAsJsonObject();
				String qaName = entry.getKey();
				String qaDescription = valueObj.get("description").getAsString();

				QualityAspect qa = new QualityAspect(qaName, qaDescription);
				getTqi().setQualityAspect(qa);
				getTqi().setWeight(qa.getName(), 0.0);
			});

			// Product Factors
			productFactors.entrySet().forEach(entry -> {
				JsonObject valueObj = entry.getValue().getAsJsonObject();
				String pfName = entry.getKey();
				String pfDescription = valueObj.get("description").getAsString();

				ProductFactor pf = new ProductFactor(pfName, pfDescription);
				getTqi().getQualityAspects().values().forEach(qa -> {
					qa.setProductFactor(pf);
					qa.setWeight(pf.getName(), 0.0);
				});
			});

			// Measures
			measures.entrySet().forEach(entry -> {
				JsonObject jsonMeasure = entry.getValue().getAsJsonObject();
				String measureName = entry.getKey();
				String measureDescription = jsonMeasure.get("description").getAsString();
				boolean positive = jsonMeasure.getAsJsonPrimitive("positive").getAsBoolean();
				Measure m = new Measure(measureName, measureDescription, positive, benchmarker);

				// Optional thresholds
				Double[] thresholds = null;
				if (jsonMeasure.getAsJsonArray("thresholds") != null) {
					thresholds = new Double[jsonMeasure.getAsJsonArray("thresholds").size()];
					for (int i = 0; i < jsonMeasure.getAsJsonArray("thresholds").size(); i++) {
						thresholds[i] = jsonMeasure.getAsJsonArray("thresholds").get(i).getAsDouble();
					}
					m.setThresholds(thresholds);
				}


				// Diagnostics
				List<Diagnostic> diagnostics = new ArrayList<>();
				JsonArray jsonDiagnostics = jsonMeasure.getAsJsonArray("diagnostics");
				jsonDiagnostics.forEach(d -> {
					JsonObject diagnostic = d.getAsJsonObject();
					String dName = diagnostic.get("name").getAsString();
					String dDescription = diagnostic.get("description").getAsString();
					String dToolName = diagnostic.get("toolName").getAsString();

					// Optional diagnostic 'eval_strategy' field
					if (diagnostic.get("eval_strategy") != null) {
						String fullClassName = diagnostic.get("eval_strategy").getAsString();
						try {
							diagnostics.add(new Diagnostic(
									dName,
									dDescription,
									dToolName,
									(IEvaluator) Class.forName(fullClassName).getConstructor().newInstance()));
						} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
							e.printStackTrace();
						}
					} else {
						diagnostics.add(new Diagnostic(dName, dDescription, dToolName, new DefaultDiagnosticEvaluator()));
					}
				});
				m.setDiagnostics(diagnostics);


				// Optional measure 'normalizer' field
				if (jsonMeasure.get("normalizer") != null && !(jsonMeasure.get("normalizer").getAsString().equals("pique.evaluation.DefaultNormalizer"))) {
					String fullClassName = jsonMeasure.get("normalizer").getAsString();
					try {
						INormalizer normalizer = (INormalizer) Class.forName(fullClassName).getConstructor().newInstance();
						m.setiNormalizer(normalizer);
					} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
						e.printStackTrace();
					}
				}

				// Optional measure 'eval_strategy' field
				if (jsonMeasure.get("eval_strategy") != null && !(jsonMeasure.get("eval_strategy").getAsString().equals("pique.evaluation.DefaultMeasureEvaluator"))) {
					String fullClassName = jsonMeasure.get("eval_strategy").getAsString();
					try {
						IEvaluator evaluator = (IEvaluator) Class.forName(fullClassName).getConstructor().newInstance();
						m.setEvaluator(evaluator);
					} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
						e.printStackTrace();
					}
				}

				// Attach measure to stated product factors
				jsonMeasure.getAsJsonArray("parents").forEach(parentName -> {
					getProductFactor(parentName.getAsString()).setMeasure(m);
					m.setParent(parentName.getAsString());
				});
			});

			// Crawl back up json inputting factor weights
			if (qualityAspects.getAsJsonObject(getAnyQualityAspect().getName()).getAsJsonObject("weights") != null) {
				qualityAspects.entrySet().forEach(entry -> {
					String qaName = entry.getKey();
					JsonObject qaWeights = entry.getValue().getAsJsonObject().getAsJsonObject("weights");
					qaWeights.entrySet().forEach(weightEntry -> {
						getQualityAspect(qaName).setWeight(weightEntry.getKey(), weightEntry.getValue().getAsDouble());
					});
				});

				tqi.getAsJsonObject(getTqi().getName()).getAsJsonObject("weights").entrySet().forEach(tqiWeightEntry -> {
					getTqi().setWeight(tqiWeightEntry.getKey(), tqiWeightEntry.getValue().getAsDouble());
				});

				// TODO: Assert that weight mappings have correct ProductFactor and Characteristics to map to and are pass by reference for characteristics (make new method)
			}

		} catch (IOException | InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
