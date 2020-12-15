package pique.model;

import pique.calibration.IBenchmarker;
import pique.calibration.IWeighter;
import pique.calibration.NaiveBenchmarker;
import pique.calibration.NaiveWeighter;
import java.util.*;

/**
 * This class contains all the appropriate information that describe a
 * Quality Model that can be used for the evaluation of a single project or
 * a benchmark of projects.
 * <p>
 * Typically, it is used in order to load the PropertySet and the CharacteristicSet
 * of the file that describes the quality model and assign their values to the
 * project (or projects) that we want to evaluate.
 */
/*
 * TODO:
 *   (1) Add checks immediately after model creation that no duplicate node names by-level exist
 */
public class QualityModel {

    // Fields
    private String name;  //The name of the QM found in the XML file
    private Tqi tqi;  // root node, the total quality evaluation, contains quality aspect objects as children
    private IBenchmarker benchmarker;
    private IWeighter weighter;

    // Constructors

    public QualityModel() { }

    public QualityModel(String name, Tqi tqi) {
        this.name = name;
        this.tqi = tqi;
        this.benchmarker = new NaiveBenchmarker();
        this.weighter = new NaiveWeighter();
    }

    /**
     * Constructor for use in deep cloning this quality model
     *
     * @param name Quality model name
     * @param tqi  Root node of quality model tree. Careful passing this by reference.
     *             Will likely want to use this.tqi.clone().
     */
    public QualityModel(String name, Tqi tqi, IBenchmarker benchmarker, IWeighter weighter) {
        this.name = name;
        this.tqi = tqi;
        this.benchmarker = benchmarker;
        this.weighter = weighter;
    }

    //region Getters and Setters
    // TODO (1.0): Change getter and setter functions to use tree traversal and reflection. Current form will cause
    //  issues when having multiple layers of the same node type (e.g. a product factor connected to a product factor)
    public IBenchmarker getBenchmarker() {
        return benchmarker;
    }

    public void setBenchmarker(IBenchmarker benchmarker) {
        this.benchmarker = benchmarker;
    }

    public Map<String, ModelNode> getDiagnostics() {
        Map<String, ModelNode> diagnostics = new HashMap<>();
        getMeasures().values().forEach(measure -> diagnostics.putAll(measure.getChildren()));
        return diagnostics;
    }

    public Map<String, ModelNode> getMeasures() {
        Map<String, ModelNode> measures = new HashMap<>();
        List<ModelNode> productFactorList = new ArrayList<>(getProductFactors().values());

        productFactorList.forEach(productFactor -> {
            productFactor.getChildren().values().forEach(measure -> {
                measures.put(measure.getName(), measure);
            });
        });

        return measures;
    }

    public ModelNode getMeasureByName(String name) {
        return getMeasures().get(name);
    }

    public ProductFactor getProductFactor(String name) {
        return (ProductFactor) getProductFactors().get(name);
    }

    @Deprecated     // Makes assumption only 1 measure per product factor. Phasing out WIP
    public ProductFactor getProductFactorByMeasureName(String measureName) {
        for (ModelNode productFactor : getProductFactors().values()) {
            if (productFactor.getChild(measureName) != null) {
                return (ProductFactor) productFactor;
            }
        }
        throw new RuntimeException("Unable to find property with child measure name " + measureName + " from QM's property nodes");
    }

    // TODO (1.0): Makes assumption all QA children are Product Factors
    public Map<String, ModelNode> getProductFactors() {
        Map<String, ModelNode> productFactors = new HashMap<>();

        getQualityAspects().values().forEach(qa -> {
            Collection<ModelNode> qaChildren = qa.getChildren().values();
            qaChildren.forEach(child -> productFactors.putIfAbsent(child.getName(), child));
        });

        return productFactors;
    }

    public QualityAspect getQualityAspectAny() {
        return (QualityAspect) getTqi().getAnyChild();
    }

    public QualityAspect getQualityAspect(String name) {
        return (QualityAspect) getTqi().getChild(name);
    }

    public Map<String, ModelNode> getQualityAspects() {
        return getTqi().getChildren();
    }

    public void setQualityAspects(Map<String, ModelNode> qualityAspects) {
        getTqi().setChildren(qualityAspects);
    }

    public void setQualityAspect(QualityAspect qualityAspect) {
        getTqi().setChild(qualityAspect);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setWeighter(IWeighter weighter) {
        this.weighter = weighter;
    }

    //endregion


    //region Methods

    /**
     * @return Deep clone of this QualityModel object
     */
    @Override
    public QualityModel clone() {
        Tqi rootNode = (Tqi) getTqi().clone();
        return new QualityModel(getName(), rootNode, getBenchmarker(), getWeighter());
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof QualityModel)) {
            return false;
        }
        QualityModel otherQm = (QualityModel) other;

        return getName().equals(otherQm.getName())
                && getTqi().equals(otherQm.getTqi());
    }

    //endregion
}
