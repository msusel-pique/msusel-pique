package pique.calibration;

import java.nio.file.Path;
import java.util.Set;

public interface IWeighter {

    Set<WeightResult> elicitateWeights(Path inFile, Path outFile);

    String getName();

}
