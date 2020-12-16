package pique.calibration;

import pique.model.ModelNode;
import pique.model.QualityModel;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

// TODO (1.0): Documentation
public class NaiveWeighter implements IWeighter {

    /**
     * Set each node's incoming edge weights equal to the average of all incoming edges.
     */
    @Override
    public Set<WeightResult> elicitateWeights(QualityModel qualityModel, Path... externalInput) {

        Set<WeightResult> weights = new HashSet<>();

        qualityModel.getAllQualityModelNodes().values().forEach(node -> {
            WeightResult weightResult = new WeightResult(node.getName());
            node.getChildren().values().forEach(child -> weightResult.setWeight(child.getName(), averageWeight(node)));
            weights.add(weightResult);
        });

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
