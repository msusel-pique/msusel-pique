package qatch.model;

import java.util.HashMap;
import java.util.Map;

/**
 * This class encapsulates all the appropriate information that describe a 
 * Quality Model that can be used for the evaluation of a single project or 
 * a benchmark of projects.
 * 
 * Typically, it is used in order to load the PropertySet and the CharacteristicSet
 * of the XML file that describes the quality model and assign their values to the 
 * project (or projects) that we want to evaluate.
 * 
 * @author Miltos
 *
 */
public class QualityModel {

	//A representation of quality model inside JVM
	private String name;  //The name of the QM found in the XML file
	private Tqi tqi;	  // root node, the total quality evaluation, contains characteristic objects as children
	private Map<String, Characteristic> characteristics = new HashMap<>();
	private Map<String, Property> properties = new HashMap<>();  // each property node has 1 associated Measure node
	private CharacteristicSet characteristics_deprecated;  //The CharacteristicSet containing all the characteristics_deprecated of the Quality Model
	private PropertySet properties_deprecated;				//The PropertySet containing all the properties of the Quality Model


	// constructor
	QualityModel() {
		this.tqi = new Tqi();
	}
	
	//Setters and Getters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PropertySet getProperties_deprecated() {
		return properties_deprecated;
	}

	public void setProperties_deprecated(PropertySet properties_deprecated) {
		this.properties_deprecated = properties_deprecated;
	}

	public CharacteristicSet getCharacteristics_deprecated() {
		return characteristics_deprecated;
	}

	public void setCharacteristics_deprecated(CharacteristicSet characteristics_deprecated) {
		this.characteristics_deprecated = characteristics_deprecated;
	}
	
	public Tqi getTqi() {
		return tqi;
	}

	public void setTqi(Tqi tqi) {
		this.tqi = tqi;
	}
	
	//TODO: Add extra methods for avoiding train expressions...
	
}
