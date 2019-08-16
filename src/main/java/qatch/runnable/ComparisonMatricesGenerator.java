package qatch.runnable;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import qatch.model.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;

/**
 * Generates template .xls comparison matrices to then be used for human data entry.
 * Use this class to generate the matrices, manually fill in the upper triangle
 * with values from [1/9...1/1...9/1], and place the .xls files in the appropriate
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
     *                E.g.: "C:\Users\name\desktop\qualityModel_csharp_description.xml"
     *             1: Path to directory to place the comparison matrices output.
     *                E.g.: "C:\Users\name\desktop\results"
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
        QualityModel qualityModel = new QualityModel();
        PropertiesAndCharacteristicsLoader qmLoader = new PropertiesAndCharacteristicsLoader(qmLocation.toString());
        qualityModel = qmLoader.importQualityModel();
        PropertySet properties = qualityModel.getProperties();
        CharacteristicSet characteristics = qualityModel.getCharacteristics();

        if (!fuzzy) { generateComparisonMatrix(properties, characteristics, outLocation, "0"); }
        else if (fuzzy) { generateFuzzyComparisonMatrix(properties, characteristics, outLocation, "-"); }
        else { throw new RuntimeException("input arg 2 did not match to 'true' or 'false'"); }

        System.out.println("...");

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
     * This method is responsible for the generation of the comparison matrices that
     * are needed for the elicitation of the weights of the quality model's characteristics.
     *
     * Typically, we have one comparison matrix for each Quality Model's characteristic.
     */
    private static void subroutineCharacteristis(PropertySet properties, CharacteristicSet characteristics, Path outLocation, String defaultChar) {

        //For each characteristic do...
        Characteristic characteristic;
        Iterator<Characteristic> iterator = characteristics.iterator();
        while(iterator.hasNext()){

            //Get the current characteristic
            characteristic = iterator.next();

            //Create a new workbook for the matrix
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet( characteristic.getName() + " Comparison Matrix");
            HSSFRow rowhead = sheet.createRow((short) 0);

            //Set the "characteristic" that this Comparison Matrix refers to
            rowhead.createCell(0).setCellValue(characteristic.getName());

            //Create the header of the xls file
            for(int i = 0; i < properties.size(); i++){
                rowhead.createCell(i+1).setCellValue(properties.get(i).getName());
            }

            Property property = new Property();
            for(int i = 0; i < properties.size(); i++){

                //Get the current Property
                property = properties.get(i);

                //Create a new row for this property and set its name
                HSSFRow row = sheet.createRow((short) i+1);
                row.createCell(0).setCellValue(property.getName());

                //Fulfill the unused cells of the matrix with the predefined value
                for(int j = 0; j <= i; j++){

                    row.createCell(j+1).setCellValue(defaultChar);
                }
            }

            //Set the name of the comparison matrix
            String filename = new File(outLocation.toFile(), characteristic.getName() + ".xls").getAbsolutePath();

            //Export the XLS file to the desired path
            FileOutputStream fileOut = null;
            try {
                fileOut = new FileOutputStream(filename);
                workbook.write(fileOut);
                fileOut.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e){
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Generation the comparison matrix that is needed for the elicitation of the TQI's weights.
     *
     * @param characteristics
     *          Set of characteristis defined by the quality model description.
     *          The rows of the TQI matrix are these characteristics
     */
    private static void subroutineTQI(CharacteristicSet characteristics, Path outLocation, String defaultChar){

        //Create a new workbook for the matrix
        // TODO: use more current approach for writing out a matrix
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("TQI Comparison Matrix");
        HSSFRow rowhead = sheet.createRow((short) 0);

        //Set the "characteristic" that this Comparison Matrix refers to
        rowhead.createCell(0).setCellValue("TQI");

        //Create the header of the xls file
        for(int i = 0; i < characteristics.size(); i++){
            rowhead.createCell(i+1).setCellValue(characteristics.get(i).getName());
        }

        //Fill the columns appropriately
        Characteristic characteristic = new Characteristic();
        for(int i = 0; i < characteristics.size(); i++){
            characteristic = characteristics.get(i);
            HSSFRow row = sheet.createRow((short) i+1);
            row.createCell(0).setCellValue(characteristic.getName());
            for(int j = 0; j <= i; j++){
                row.createCell(j+1).setCellValue(defaultChar);
            }
        }

        //Set the name of the comparison matrix
        File filename = new File(outLocation.toFile(), "TQI.xls");

        //Export the XLS file to the appropriate path (R's working directory)
        FileOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(filename);
            workbook.write(fileOut);
            fileOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            System.out.println(e.getMessage());
        }

    }


}
