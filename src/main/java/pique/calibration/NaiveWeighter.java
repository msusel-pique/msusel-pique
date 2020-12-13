package pique.calibration;

import pique.model.ModelNode;
import pique.model.QualityModel;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

// TODO (1.0): Documentation
public class NaiveWeighter implements IWeighter {

    /**
     * Set each node's incoming edge weights equal to the average of all incoming edges.
     * // TODO (1.0): Implement as recursive BFS
     */
    @Override
    public Set<WeightResult> elicitateWeights(QualityModel qualityModel, Path... externalInput) {

        Set<WeightResult> weights = new HashSet<>();

        // TQI
        ModelNode tqi = qualityModel.getTqi();
        WeightResult tqiWeightResult = new WeightResult(tqi.getName());
        for (ModelNode child : tqi.getChildren().values()) {
            tqiWeightResult.setWeight(child.getName(), averageWeight(tqi));
        }
        weights.add(tqiWeightResult);

        // Quality Aspects
        for (ModelNode qaNode : tqi.getChildren().values()) {
            WeightResult qaWeightResult = new WeightResult(qaNode.getName());

            for (ModelNode child : qaNode.getChildren().values()) {
                qaWeightResult.setWeight(child.getName(), averageWeight(qaNode));
            }

            weights.add(qaWeightResult);
        }

        // Product Factors
        Set<ModelNode> productFactors = new HashSet<>(qualityModel.getProductFactors().values());
        for (ModelNode productFactor : productFactors) {

            WeightResult pfWeightResult = new WeightResult(productFactor.getName());
            for (ModelNode child : productFactor.getChildren().values()) {
                pfWeightResult.setWeight(child.getName(), averageWeight(productFactor));
            }

            weights.add(pfWeightResult);
        }

        return weights;
    }

    @Override
    public String getName() {
        return this.getClass().getCanonicalName();
    }

    private double averageWeight(ModelNode currentNode) {
        return 1.0 / (double)currentNode.getChildren().size();
    }
}
