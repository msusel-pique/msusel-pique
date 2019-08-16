package qatch.calibration;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
	
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
			// TODO: add non-Windows functionality (very simple)
			throw new RuntimeException("Non-Windows OS functionality not yet implemented for R script execution");
		}

		pb.redirectErrorStream(true);
		Process p = null;
		// run the tool
		try { p = pb.start(); }
		catch (IOException e) { e.printStackTrace(); }

		try {
		    assert p != null;
		    // TODO: find way to stop waiting for R script if R gets hung up some how (e.g. 0-values in APH matrix)
			p.waitFor();
		}
		catch (InterruptedException e) {e.printStackTrace(); }
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
