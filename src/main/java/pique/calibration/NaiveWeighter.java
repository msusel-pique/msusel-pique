package pique.calibration;

import com.opencsv.CSVReader;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

// TODO (1.0): Documentation
public class NaiveWeighter implements IWeighter {

    @Override
    public Set<WeightResult> elicitateWeights() {
        throw new NotImplementedException();
    }

    @Override
    public String getName() {
        return "pique.calibration.NaiveWeighter";
    }
}
