package pique.calibration;

import java.nio.file.Path;
import java.util.Set;

// TODO (1.0): Documentation
public interface IWeighter {

    Set<WeightResult> elicitateWeights();

    String getName();

}
