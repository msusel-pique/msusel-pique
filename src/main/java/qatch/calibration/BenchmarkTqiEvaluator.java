package qatch.calibration;

import qatch.evaluation.Project;
import qatch.model.Tqi;

import java.util.Iterator;

/**
 * This class is responsible for calculating the TQI of a set of projects.
 * 
 * Typically, it does the following:
 *    1. It clones the Tqi object of the quality model into the tqi field
 *       of each project.
 *    2. It calls the calculateTqi() method for each project of the desired
 *       set of projects, that calculates the Tqi of a single project.
 * 
 * @author Miltos
 * 
 * TODO: Remove the clone method - Add a Cloner class instead!!
 *
 */
@Deprecated
public class BenchmarkTqiEvaluator {
	
	/**
	 * A method for cloning the TQI object of the Quality Model to each Project object
	 * of the desired BencjhmarkProjects.
	 * 
	 * @param projects : The set of projects under evaluation
	 * @param tqi      : The TQI Object of the imported Quality Model
	 * @throws CloneNotSupportedException
	 */
	@Deprecated
	public void cloneTqi(BenchmarkProjects projects, Tqi tqi) throws CloneNotSupportedException{
		
		//Create an iterator of the available projects
		Iterator<Project> iterator = projects.iterator();
		
		//For each project do...
		while(iterator.hasNext()){
			
			//Get the current project
			Project project = iterator.next();
			
			//Copy the TQI object of the QM to the tqi field of this project
//			project.setTqi((Tqi)tqi.clone());
		}
		
	}
}
