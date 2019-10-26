package qatch.calibration;

import qatch.analysis.IFindingsAggregator;
import qatch.analysis.IMetricsAggregator;
import qatch.evaluation.Project;
import qatch.model.Property;
import qatch.model.PropertySet;

import java.util.Iterator;

/**
 * This class is responsible for calculating the fields: normalizer,
 * value and normValue of the properties of a set of projects 
 * (i.e. BenchmarkProjects). It takes advantage of the data that
 * are stored in the objects:
 *    - IssueSet
 *    - MetricSet
 * of each project, in order to calculate those fields.
 * 
 * Typically, it iterates through the list of projects and for 
 * each project it calls the aggregate() method of the single 
 * project aggregators:
 * 	  - IMetricsAggregator
 *    - IFindingsAggregator
 * which are responsible for the "aggregation" of a single project.
 *
 */
//TODO: Check for parallel alternative - if it is needed and only if it is possible
@Deprecated
public class BenchmarkAggregator {
	
	/**
	 * This method is responsible for the aggregation of the properties of a set 
	 * of projects. The whole functionality of the aggregator is summarized in
	 * three steps:
	 * 	1. Clone the PropertySet of the model to the PropertySet of each project
	 *  2. Call single project aggregators for each project of the project set
	 *  3. Normalize the values of the properties of the set of projects
	 *  
	 */
	
	public BenchmarkProjects aggregateProjects(BenchmarkProjects projects,
											   PropertySet properties,
											   IMetricsAggregator metricsAgg,
											   IFindingsAggregator findingsAgg) throws CloneNotSupportedException{
		
		//Clone the properties of the Quality Model on each project
		cloneProperties(projects, properties);
		
		//Aggregate all the projects
		projects.getProjects().forEach(p -> {
			metricsAgg.aggregate(p);
			findingsAgg.aggregate(p);
		});

		//Normalize all the values
		normalizeProperties(projects);
		
		//Optional
		return projects;
	}
	
	/**
	 * This method is responsible for copying the PropertySet containing
	 * all the properties of the Quality Model, into the PropertySet of 
	 * each project of the BenchmarkProject object. (deep cloning is used)
	 */
	@Deprecated
	private void cloneProperties(BenchmarkProjects projects, PropertySet properties) throws CloneNotSupportedException{
		
		//Create an iterator of the available projects
		Iterator<Project> iterator = projects.iterator();
		
		while(iterator.hasNext()){
			
			//Get the current project
			Project project = iterator.next();
			
			//For each property do...
			for(int i = 0; i < properties.size(); i++){
				
				//Clone the property and add it to the PropertySet of the current project
//				Property p = (Property)properties.get(i).clone();
				Property p = (Property)properties.get(i);
				project.addProperty(p);
			}
		}
		
	}
	
	/**
	 * This method is responsible for calculating the normalized value (normValue) of
	 * the properties of each project found inside a set of projects.
	 */
	private void normalizeProperties(BenchmarkProjects projects){
		
		//Iterate through all the projects
		Iterator<Project> iterator = projects.iterator();
		
		while(iterator.hasNext()){
			
			//Get the current project
			Project project = iterator.next();
			
			//For each property do...
			for(int i = 0; i < project.getProperties_depreicated().size(); i++){
				
				Property property =  project.getProperties_depreicated().get(i);
				property.getMeasure().calculateNormValue();
				
			}
		}
	}
}
