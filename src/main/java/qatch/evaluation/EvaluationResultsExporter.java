package qatch.evaluation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import qatch.calibration.BenchmarkProjects;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class EvaluationResultsExporter {

	public static String BASE_DIR = new File(System.getProperty("user.dir")).getAbsolutePath();
	public static String BENCH_RESULT_PATH = new File(BASE_DIR + "/Results/Evaluation/BenchmarkResults").getAbsolutePath();
	public static String WORKSPACE_RESULT_PATH = new File(BASE_DIR + "/Results/Evaluation/WorkspaceResults").getAbsolutePath();
	public static String SINGLE_PROJ_RESULT_PATH = new File(BASE_DIR + "/Results/Evaluation/SingleProjectResults").getAbsolutePath();

	/**
	 * This method is used in order to export the results of the projects' evaluation
	 * to JSON format.
     */
	@Deprecated
	public static void exportProjectsToJson(BenchmarkProjects projects, String path){
		
		//Instantiate a Json Parser
		Gson gson = new Gson();
		
		//Create the Json String of the projects
		String json = gson.toJson(projects);
		
		//Save the results
		try{
			FileWriter writer = new FileWriter(path);
			writer.write(json);
			writer.close();
		}catch(IOException e){
			System.out.println(e.getMessage());
		}
	}

	/**
	 * This method is used in order to export the results of the evaluation of a certain
	 * project to JSON format.
	 */
	public static void exportProjectToJson(Project project, Path path){

		// ensure target path directory exists
		path.getParent().toFile().mkdirs();

		//Instantiate a Json Parser
		Gson gson = new GsonBuilder()
				.disableHtmlEscaping()
				.excludeFieldsWithoutExposeAnnotation()
				.create();
		
		//Create the Json String of the projects
		String json = gson.toJson(project);
		
		//Save the results
		try{
			FileWriter writer = new FileWriter(path.toString());
			writer.write(json);
			writer.close();
		}catch(IOException e){
			System.out.println(e.getMessage());
		}
	}
}
