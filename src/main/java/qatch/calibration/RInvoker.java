package qatch.calibration;

import java.io.File;
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
class RInvoker {

	public enum Script { AHP, FAPH, THRESHOLD }


	// TODO: assert that expected behavior occurs after running script (e.g. the file was created)
	/**
	 * Run an R script according to its enumeration.
	 * In most cases the input and output parameters should point to a directory containing the needed
	 * files for R execution, not the file itself. Check the relevant R script for details.
	 *
	 * @param script
	 * 		Enumeration of the descired R script to run
	 * @param input
	 * 		Path to the directory containin the relevant parameters needed for the r script execution.
	 * 	C:\Users\davidrice3\Repository\msusel-qatch\msusel-qatch\
	 * @param output
	 * 		Path to the desired directory to place results in. The directory is created if it does not yet exist.
	 * 		The path should be absolute.
	 */
	static void executeRScript(Script script, String input, String output){

		new File(output).mkdirs();

		// TODO: Source Rscript bin in workspace-independent way
		String rBinPath = findRRunner().toString();
		Path scriptPath = new File(getRScriptResource(script).getFile()).toPath();

		ProcessBuilder pb;
		String cli;

		if(System.getProperty("os.name").contains("Windows")){ cli = "cmd.exe"; }
		else { cli = "sh"; }

		pb = new ProcessBuilder(cli, "/c", rBinPath, scriptPath.toString(), input, output);

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


	static URL getRScriptResource(Script choice) {
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


	/**
	 * Search the OS file system for the runnable RScript program.
	 *
	 * @return
	 * 		The path to the RScript executable
	 */
	private static Path findRRunner() {
		Path rPath;

		// TODO: progmatically find the path in a workspace independent way rather than fixed strings
		if (System.getProperty("os.name").contains("Windows")) {
			rPath = Paths.get("C:/Program Files/R/R-3.6.1/bin/x64/Rscript.exe");
		}
		else if (System.getProperty("os.name").contains("Mac")) {
			rPath = Paths.get("/Library/Frameworks/R.framework/Versions/3.6/Resources/bin/Rscript");
		}
		else {
			throw new RuntimeException("Path to Rscript executable not yet defined for this operating system or " +
					"R is installed in a non-default location.");
		}

		return rPath;
	}
}
