package qatch.calibration;

import java.util.Iterator;

import qatch.evaluation.Project;
import qatch.model.Tqi;

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
public class BenchmarkTqiEvaluator {
	
	/**
	 * This method implements the whole functionality of this class.
	 * Its algorithm is pretty straightforward if you read the inline 
	 * comments.
	 */
	public void evaluateProjects(BenchmarkProjects projects, Tqi tqi) throws CloneNotSupportedException{
		
		//Clone the quality model's tqi object to each project of the BenchmarkProjects
		cloneTqi(projects, tqi);
		
		//Iterate through the set of projects
		Iterator<Project> iterator = projects.iterator();
		while(iterator.hasNext()){
			Project project = iterator.next();
			project.calculateTQI();
		}
	}
	
	/**
	 * A method for cloning the TQI object of the Quality Model to each Project object
	 * of the desired BencjhmarkProjects.
	 * 
	 * @param projects : The set of projects under evaluation
	 * @param tqi      : The TQI Object of the imported Quality Model.
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
