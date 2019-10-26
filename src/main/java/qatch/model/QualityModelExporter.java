package qatch.model;

import com.google.gson.Gson;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

/**
 * This class is responsible for the exportation of the XML file that
 * represents the Quality Model, as a result of calibration and weight
 * elicitation.
 * 
 * Typically, it combines the three individual exporters in order to 
 * export all the information concerning the Quality Model, into a single 
 * XML or JSON file.
 * 
 * @author Miltos
 *
 */
@Deprecated
public class QualityModelExporter {

}
