package pique.calibration;

import pique.model.ModelNode;
import pique.model.QualityModel;

import java.nio.file.Path;
import java.util.Set;

// TODO (1.0): Documentation
public interface IWeighter {

    Set<WeightResult> elicitateWeights(QualityModel qualityModel, Path... externalInput);

    String getName();

}
