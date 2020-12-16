package pique.model;

import pique.calibration.IBenchmarker;
import pique.calibration.IWeighter;
import pique.calibration.NaiveBenchmarker;
import pique.calibration.NaiveWeighter;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.lang.reflect.Type;
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

    private String name;
    private Tqi tqi;
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

    /**
     * BFS through QM tree while adding nodes to a mapping (if a node with the same name does not already exist)
     */
    public Map<String, ModelNode> getAllQualityModelNodes() {

        Map<String, ModelNode> allModelNodes = new HashMap<>();

        Queue<ModelNode> queue = new LinkedList<>();
        queue.add(getTqi());
        allModelNodes.put(getTqi().getName(), getTqi());
        getTqi().setVisited(true);

        while (!queue.isEmpty()) {
            ModelNode node = queue.remove();
            while (node.getChildren().size() > 0 && node.getChildren().values().stream().anyMatch(modelNode -> !modelNode.isVisited())) {
                ModelNode child = node.getChildren().values().stream()
                            .filter(modelNode -> !modelNode.isVisited())
                            .findFirst()
                            .orElseThrow(RuntimeException::new);
                allModelNodes.put(child.getName(), child);
                child.setVisited(true);
                queue.add(child);
            }
        }

        // TODO (1.1): Much better way to clear 'visited' state. Lots of BFS options.
        allModelNodes.values().forEach(node -> node.setVisited(false));

        return allModelNodes;
    }

    public IBenchmarker getBenchmarker() {
        return benchmarker;
    }

    public void setBenchmarker(IBenchmarker benchmarker) {
        this.benchmarker = benchmarker;
    }

    public ModelNode getDiagnostic(String name) {
        return getDiagnostics().get(name);
    }

    public Map<String, ModelNode> getDiagnostics() {

        Map<String, ModelNode> diagnosticNodes = new HashMap<>();
        Map<String, ModelNode> allModelNodes = new HashMap<>();

        Queue<ModelNode> queue = new LinkedList<>();
        queue.add(getTqi());

        allModelNodes.put(getTqi().getName(), getTqi());
        getTqi().setVisited(true);

        while (!queue.isEmpty()) {

            ModelNode node = queue.remove();
            while (node.getChildren().size() > 0 && node.getChildren().values().stream().anyMatch(modelNode -> !modelNode.isVisited())) {
                ModelNode child = node.getChildren().values().stream()
                        .filter(modelNode -> !modelNode.isVisited())
                        .findFirst()
                        .orElseThrow(RuntimeException::new);

                allModelNodes.put(child.getName(), child);
                if (child instanceof Diagnostic) diagnosticNodes.put(child.getName(), child);
                child.setVisited(true);
                queue.add(child);
            }
        }

        // TODO (1.1): Much better ways to clear 'visited' state. Lots of BFS options.
        allModelNodes.values().forEach(node -> node.setVisited(false));

        return diagnosticNodes;
    }

    public ModelNode getMeasure(String name) {
        return getMeasures().get(name);
    }

    public Map<String, ModelNode> getMeasures() {
        Map<String, ModelNode> measureNodes = new HashMap<>();
        Map<String, ModelNode> allModelNodes = new HashMap<>();

        Queue<ModelNode> queue = new LinkedList<>();
        queue.add(getTqi());

        allModelNodes.put(getTqi().getName(), getTqi());
        getTqi().setVisited(true);

        while (!queue.isEmpty()) {

            ModelNode node = queue.remove();
            while (node.getChildren().size() > 0 && node.getChildren().values().stream().anyMatch(modelNode -> !modelNode.isVisited())) {
                ModelNode child = node.getChildren().values().stream()
                        .filter(modelNode -> !modelNode.isVisited())
                        .findFirst()
                        .orElseThrow(RuntimeException::new);

                allModelNodes.put(child.getName(), child);
                if (child instanceof Measure) measureNodes.put(child.getName(), child);
                child.setVisited(true);
                queue.add(child);
            }
        }

        // TODO (1.1): Much better way to clear 'visited' state. Lots of BFS options.
        allModelNodes.values().forEach(node -> node.setVisited(false));

        return measureNodes;
    }

    public ProductFactor getProductFactor(String name) {
        return (ProductFactor) getProductFactors().get(name);
    }

    public Map<String, ModelNode> getProductFactors() {
        Map<String, ModelNode> pfNodes = new HashMap<>();
        Map<String, ModelNode> allModelNodes = new HashMap<>();

        Queue<ModelNode> queue = new LinkedList<>();
        queue.add(getTqi());

        allModelNodes.put(getTqi().getName(), getTqi());
        getTqi().setVisited(true);

        while (!queue.isEmpty()) {

            ModelNode node = queue.remove();
            while (node.getChildren().size() > 0 && node.getChildren().values().stream().anyMatch(modelNode -> !modelNode.isVisited())) {
                ModelNode child = node.getChildren().values().stream()
                        .filter(modelNode -> !modelNode.isVisited())
                        .findFirst()
                        .orElseThrow(RuntimeException::new);

                allModelNodes.put(child.getName(), child);
                if (child instanceof ProductFactor) pfNodes.put(child.getName(), child);
                child.setVisited(true);
                queue.add(child);
            }
        }

        // TODO (1.1): Much better way to clear 'visited' state. Lots of BFS options.
        allModelNodes.values().forEach(node -> node.setVisited(false));

        return pfNodes;
    }

    public QualityAspect getQualityAspect(String name) {
        return (QualityAspect)getQualityAspects().get(name);
    }

    public Map<String, ModelNode> getQualityAspects() {
        Map<String, ModelNode> qaNodes = new HashMap<>();
        Map<String, ModelNode> allModelNodes = new HashMap<>();

        Queue<ModelNode> queue = new LinkedList<>();
        queue.add(getTqi());

        allModelNodes.put(getTqi().getName(), getTqi());
        getTqi().setVisited(true);

        while (!queue.isEmpty()) {

            ModelNode node = queue.remove();
            while (node.getChildren().size() > 0 && node.getChildren().values().stream().anyMatch(modelNode -> !modelNode.isVisited())) {
                ModelNode child = node.getChildren().values().stream()
                        .filter(modelNode -> !modelNode.isVisited())
                        .findFirst()
                        .orElseThrow(RuntimeException::new);

                allModelNodes.put(child.getName(), child);
                if (child instanceof QualityAspect) qaNodes.put(child.getName(), child);
                child.setVisited(true);
                queue.add(child);
            }
        }

        // TODO (1.1): Much better way to clear 'visited' state. Lots of BFS options using better data structures or
        //  recursive approach. No time for now :).
        allModelNodes.values().forEach(node -> node.setVisited(false));

        return qaNodes;
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
