package qatch.runnable;

import com.opencsv.CSVWriter;
import qatch.model.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Generates template .csv comparison matrices to then be used for human data entry.
 * Use this class to generate the matrices, manually fill in the upper triangle
 * with values from [1/9...1/1...9/1], and place the .csv files in the appropriate
 * directory before attempting quality model derivation.
 *
 * This class acts as its own application and could potentially be moved out of the
 * qatch framework.  I currently argue it is a good tool to keep packaged with the framework
 * due to its coupling with model derivation / calibration classes
 */
public class ComparisonMatricesGenerator {

    /**
     * Main method entry
     *
     * @param args configuration array in the following order:
     *             0: Path to the quality model description .xml file. Can be relative or full path.
     *                E.g.: "C:/Users/name/desktop/qualityModel_csharp_description.xml"
     *             1: Path to directory to place the comparison matrices output.
     *                E.g.: "C:/Users/name/desktop/results"
     *             2: [true | false] flag to determine whether to run the fuzzy AHP strategy
     */
    public static void main(String[] args) {

        Path qmLocation;
        Path outLocation;
        boolean fuzzy;

        /*
         * initialize
         */
        if (args == null || args.length != 3) {
            throw new RuntimeException("Incorrect input parameters given. Be sure to include " +
                    "\n\t(0) Path to the quality model description .xml file" +
                    "\n\t(1) Path to directory to place the comparison matrices output" +
                    "\n\t(2) [true | false] flag to determine whether to run the fuzzy AHP strategy");
        }

        String qmDescription = args[0];
        String output = args[1];

        File qmFile = new File(qmDescription);
        File outFile = new File(output);

        if (!qmFile.exists() || !qmFile.isFile()) {
            throw new RuntimeException("Input arg 0, quality model description file: " + qmDescription +
                    "either doesn't exist or is not a file");
        }
        if (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("false")) {
            fuzzy = Boolean.parseBoolean(args[2]);
        }
        else { throw new RuntimeException("inputArgs[2] did not match 'true' or 'false'"); }
        outFile.mkdirs();

        qmLocation = qmFile.toPath();
        outLocation = outFile.toPath();

        /*
         * generate faux quality model to hold properties and characteristics representation
         */
        PropertiesAndCharacteristicsLoader qmLoader = new PropertiesAndCharacteristicsLoader(qmLocation.toString());
        QualityModel qualityModel = qmLoader.importQualityModel();
        PropertySet properties = qualityModel.getProperties_deprecated();
        CharacteristicSet characteristics = qualityModel.getCharacteristics_deprecated();

        if (!fuzzy) { generateComparisonMatrix(properties, characteristics, outLocation, "0"); }
        else if (fuzzy) { generateFuzzyComparisonMatrix(properties, characteristics, outLocation, "-"); }
        else { throw new RuntimeException("input arg 2 did not match to 'true' or 'false'"); }

    }

    private static void generateComparisonMatrix(PropertySet properties, CharacteristicSet characteristics, Path outLocation, String defaultChar) {
        subroutineTQI(characteristics, outLocation, defaultChar);
        subroutineCharacteristis(properties, characteristics, outLocation, defaultChar);
    }

    private static void generateFuzzyComparisonMatrix(PropertySet properties, CharacteristicSet characteristics, Path outLocation, String defaultChar) {
        // TODO: impliment fuzzy comparison matrix generation
        throw new NotImplementedException();
    }

    /**
     * Create pairwise comparison matrix in CSV format with lower diagonal filled in with a default value.
     *
     * @param name
     *      Name of matrix -- the value in cell(0,0).
     * @param comparitors
     *      One dimensional ordered list of the pairwise comparisons to make. This constitutes the rows and columns.
     * @param defaultChar
     *      The value to fill the lower diagonal cells with.
     * @param outLocation
     *      Directory to pace the generated matrix in.
     * @return
     *      The path to the .csv file.
     */
    static Path makeCsvComparisonMatrix(String name, String[] comparitors, String defaultChar, Path outLocation) {

        outLocation.toFile().mkdirs();
        File output = new File(outLocation.toFile(), name + ".csv");
        try {
            FileWriter fw = new FileWriter(output);
            CSVWriter writer = new CSVWriter(fw);

            // build rows of string arrays to eventually feed to writer
            ArrayList<String[]> csvRows = new ArrayList<>();

            // header
            String[] header = new String[comparitors.length + 1];
            header[0] = name;
            System.arraycopy(comparitors, 0, header, 1, comparitors.length);
            csvRows.add(header);

            // additional rows, set names and size
            for (String comparitor : comparitors) {
                String[] row = new String[comparitors.length + 1];
                row[0] = comparitor;
                csvRows.add(row);
            }

            // additional rows, set lower triangle to default character
            for (int rowNum = 1; rowNum < csvRows.size(); rowNum++) {
                String[] currentRow = csvRows.get(rowNum);
                for (int j = 1; j <= rowNum; j++) {
                    currentRow[j] = defaultChar;
                }
            }

            csvRows.forEach(writer::writeNext);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return output.toPath();
    }


    /**
     * This method is responsible for the generation of the comparison matrices that
     * are needed for the elicitation of the weights of the quality model's characteristics.
     *
     * Typically, we have one comparison matrix for each Quality Model's characteristic.
     */
    static void subroutineCharacteristis(PropertySet properties, CharacteristicSet characteristics, Path outLocation, String defaultChar) {

        // set the properties to compare
        String[] propertyComparitors = new String[properties.size()];
        for (int i = 0; i < properties.size(); i++) {
            propertyComparitors[i] = properties.get(i).getName();
        }

        //For each characteristic do...
        Characteristic characteristic;
        Iterator<Characteristic> iterator = characteristics.iterator();
        while(iterator.hasNext()){

            //Get the current characteristic
            characteristic = iterator.next();

            // run matrix generation
            makeCsvComparisonMatrix(characteristic.getName(), propertyComparitors, defaultChar, outLocation);
        }
    }

    /**
     * Generate the comparison matrix that is needed for the elicitation of the TQI's weights.
     *
     * @param characteristics
     *          Set of characteristis defined by the quality model description.
     *          The rows of the TQI matrix are these characteristics
     * @return the path to the comparison matrix file
     */
    static Path subroutineTQI(CharacteristicSet characteristics, Path outLocation, String defaultChar) {
        String name = "tqi";
        String[] comparitors = new String[characteristics.getCharacteristics().size()];
        for (int i = 0; i < characteristics.getCharacteristics().size(); i++) {
            comparitors[i] = characteristics.get(i).getName();
        }

        return makeCsvComparisonMatrix(name, comparitors, defaultChar, outLocation);
    }
}
