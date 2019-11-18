package qatch.calibration;

import qatch.evaluation.Project;
import qatch.evaluation.ProjectEvaluator;

import java.util.Iterator;

/**
 * This class is responsible for evaluating the properties of 
 * a set of projects (e.g. Benchmark Projects).
 * 
 * It creates a single project evaluator and applies it on
 * each project found in a BenchmarkProjects object in order
 * to calculate the eval field of each Property.
 */
@Deprecated
public class BenchmarkEvaluator {
	
	public void evaluateProjects(BenchmarkProjects projects){
		//Create a single project property evaluator
		ProjectEvaluator evaluator = new ProjectEvaluator();
		
		//Iterate through the set of projects
		Iterator<Project> iterator = projects.iterator();
		double progress = 0;
		while(iterator.hasNext()){
			//Get the current project
			Project project = iterator.next();
			//Evaluate all its properties
			evaluator.evaluateProjectProperties(project);
			progress++;
		}
	}
	

}
