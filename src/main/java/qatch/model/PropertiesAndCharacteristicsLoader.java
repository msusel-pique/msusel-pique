package qatch.model;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

@Deprecated
public class PropertiesAndCharacteristicsLoader {

	private String xmlPath;				//The exact path where the XML file that contains the QM description is placed

	//Constructors
	public PropertiesAndCharacteristicsLoader(){
		this.xmlPath = "";
	}
	public PropertiesAndCharacteristicsLoader(String xmlPath){
		this.xmlPath = xmlPath;
	}
	
	//Setters and Getters
	public String getXmlPath() {
		return xmlPath;
	}
	public void setXmlPath(String xmlPath) {
		this.xmlPath = xmlPath;
	}
	
	
	/**
	 * This method parses the <characteristics> node of the quality model's XML file
	 * and returns a CharacteristicSet object that contains all the characteristics
	 * found in the XML file.
	 */
	public static CharacteristicSet loadCharacteristicsNode(Element characteristicsNode){
		
		//Create a new CharacteristicSet object
		CharacteristicSet characteristics = new CharacteristicSet();
		
		//Get a list with all the characteristic nodes of the xml file
		List<Element> charNodes = characteristicsNode.getChildren();
		
		//For each characteristic node do ...
		for(Element charNode : charNodes){
			
			//Create a new Characteristic object
			Characteristic characteristic = new Characteristic();
			
			//Set the metadata of the current characteristic
			characteristic.setName(charNode.getAttributeValue("name"));
			characteristic.setStandard(charNode.getAttributeValue("standard"));
			characteristic.setDescription(charNode.getAttributeValue("description"));
			
			//Add the characteristic to the CharacteristicSet
			characteristics.addCharacteristic(characteristic);
		}
		
		//Return the characteristics of the quality model
		return characteristics;
	}
}
