package qatch.calibration;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchService;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.util.List;
import java.nio.file.*;
	
/**
 * This class is responsible for executing R scripts.
 * 
 * Basically, it contains methods that:
 * 
 * 	  1. Call R for a certain R script.
 *    2. Wait until R finishes the processing and create or 
 *       modify a file in the desired folder.
 */	
public class RInvoker {

	public enum Script { AHP, FAPH, THRESHOLD }
	
	//Fixed paths
	public static final Path R_WORK_DIR = Paths.get(System.getProperty("user.dir"), "r_working_directory");
	// TODO: Source Rscript bin in workspace-independent way
	public static Path R_BIN_PATH = Paths.get("C:/Program Files/R/R-3.6.1/bin/x64/Rscript.exe");


	public void executeRScript(Path rPath, Path scriptPath, String args){

		ProcessBuilder pb;
		if(System.getProperty("os.name").contains("Windows")){
			pb = new ProcessBuilder(
			        "cmd.exe",
                    "/c",
                    rPath.toString(),
                    scriptPath.toString(),
                    args
            );
		} else {
			// TODO: add non-Windows functionality
			throw new RuntimeException("Non-Windows OS functionality not yet implemented for R script execution");
		}

		pb.redirectErrorStream(true);
		Process p = null;
		// run the tool
		try { p = pb.start(); }
		catch (IOException e) { e.printStackTrace(); }

		try {
		    assert p != null;
			p.waitFor();
		}
		catch (InterruptedException e) {e.printStackTrace(); }
	}
	
	/**
	 * A method for executing the R script that calculates the thresholds 
	 * of the properties.
	 */
	public void executeRScriptForThresholds(Path rPath, Path scriptPath, String args) throws InterruptedException{
		
		//Invoke the appropriate R script for threshold extraction - Use the fixed paths
		executeRScript(rPath, scriptPath, args);

		//Wait for the RScript.exe to finish the analysis by polling the directory for changes
		Path resultsPath = Paths.get(args);

		try {
			//Create a directory watcher that watches for certain events
			WatchService watcher = resultsPath.getFileSystem().newWatchService();
			resultsPath.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY );
			WatchKey watchKey = watcher.take();

			//Poll the directory for certain events
			//Wake up the thread when a directory or a file is created, modified or deleted in the desired directory
			List<WatchEvent<?>> events = watchKey.pollEvents();
			for(WatchEvent event : events){
				if(event.kind() == StandardWatchEventKinds.ENTRY_CREATE){
					System.out.println("* Created: " + event.context().toString());
				}
				if(event.kind() == StandardWatchEventKinds.ENTRY_DELETE){
					System.out.println("* Deleted: " + event.context().toString());
				}
				if(event.kind() == StandardWatchEventKinds.ENTRY_MODIFY){
					System.out.println("* Modified: " + event.context().toString());
				}
			}
		}
		catch (IOException e) { System.out.println(e.getMessage()); }
	}
	
	/**
	 * A method for executing the R script that calculates the weights 
	 * of the model.
	 */
	// TODO: Check if okay to merge this with executeRScriptForThresholds. Looks like same procedure
	public void executeRScriptForWeightsElicitation(Path rPath, Path scriptPath, String args) throws InterruptedException{

		//Invoke the appropriate R script for threshold extraction - Use the fixed paths
		
		executeRScript(rPath, scriptPath, args);

		//Wait for the RScript.exe to finish the analysis by polling the directory for changes
		Path resultsPath = Paths.get(args);
		
		try {
			//Create a directory watcher that watches for certain events
			WatchService watcher = resultsPath.getFileSystem().newWatchService();
			resultsPath.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY );
			WatchKey watchKey = watcher.take();
			
			//Poll the directory for certain events
			//Wake up the thread when a directory or a file is created, modified or deleted in the desired directory
			List<WatchEvent<?>> events = watchKey.pollEvents();
			for(WatchEvent event : events){
				if(event.kind() == StandardWatchEventKinds.ENTRY_CREATE){
					System.out.println("* Created: " + event.context().toString());
				}
				if(event.kind() == StandardWatchEventKinds.ENTRY_DELETE){
					System.out.println("* Deleted: " + event.context().toString());
				}
				if(event.kind() == StandardWatchEventKinds.ENTRY_MODIFY){
					System.out.println("* Modified: " + event.context().toString());
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
	}

	public static URL getRScriptResource(Script choice) {
		URL resource;
		switch (choice) {
			case AHP:
				resource = RInvoker.class.getClassLoader().getResource("r_scripts/ahpWeightElicitation.R");
				break;
			case FAPH:
				resource = RInvoker.class.getClassLoader().getResource("r_scripts/fahpWeightElicitator.R");
				break;
			case THRESHOLD:
				resource = RInvoker.class.getClassLoader().getResource("r_scripts/thresholdsExtractor.R");
				break;
			default:
				throw new RuntimeException("Invalid choice enum given: [" + choice.name() + "]");
		}
		return resource;
	}
}
