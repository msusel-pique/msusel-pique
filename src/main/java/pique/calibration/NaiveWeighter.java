package pique.calibration;

import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class NaiveWeighter implements IWeighter {
    @Override
    public Set<WeightResult> elicitateWeights(Path inFile, Path outFile) {
        // Precondition checks
        if (!inFile.toFile().isDirectory()) {
            throw new RuntimeException("Parameter comparisonMatricesDirectory must be a directory");
        }
        if (Objects.requireNonNull(inFile.toFile().listFiles()).length < 1) {
            throw new RuntimeException("At least one file must exist in comparisonMatricesDirectory");
        }
        outFile.toFile().mkdirs();

        // Transform into WeightResults objects
        Set<WeightResult> weightResults = new HashSet<>();

        // Read data
        File[] matrcies = inFile.toFile().listFiles();
        assert matrcies != null;
        for (File currentMatrix : matrcies) {
            try {
                File inputFile = new File(currentMatrix.toString());

                // Read existing file header
                CSVReader reader = new CSVReader(new FileReader(inputFile), ',');
                List<String[]> csvBody = reader.readAll();

                String[] header = csvBody.get(0);
                WeightResult currentFactor = new WeightResult(header[0]);
                int column = 0;

                for (String headerEntry : header) {
                    if (column == 0) column++;
                    else {
                        currentFactor.setWeight(headerEntry, 0.0);
                        column++;
                    }
                }

                weightResults.add(currentFactor);
            }
            catch (IOException e) { e.printStackTrace(); }
        }

        // Run function on weightresult objects
        weightResults.forEach(weightResult -> {
            int numChildren = weightResult.getWeights().size();
            double equalWeight =  (1.0 / (double) numChildren);
            weightResult.getWeights().replaceAll( (k, v) -> v = equalWeight );
        });

        return weightResults;
    }

    @Override
    public String getName() {
        return "NaiveWeighter";
    }
}