package qatch.calibration;

import qatch.utility.FileUtility;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
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
	 *
	 * @param script
	 * 		Enumeration of the descired R script to run
	 * @param input
	 * 		Path to the directory containin the relevant parameters needed for the r script execution.
	 * 		E.g. C:\Users\myname\Repository\msusel-qatch\msusel-qatch\
	 * 		Can be relative or full path.
	 * @param output
	 * 		Path to directory to place the files resulting from R execution.
	 * 		Can be realive or full path.
	 */
	static void executeRScript(Script script, Path input, Path output, Path tempResourcesDirectory){

		input = input.toAbsolutePath();
		output = output.toAbsolutePath();

		new File(output.toString()).mkdirs();

		// TODO: Source Rscript bin in workspace-independent way
		String rBinPath = findRRunner().toString();
		Path scriptPath = getRScriptResource(script, tempResourcesDirectory);

		ProcessBuilder pb;
		String cli;

		if(System.getProperty("os.name").contains("Windows")){ cli = "cmd.exe"; }
		else { cli = "sh"; }

		pb = new ProcessBuilder(cli, "/c", rBinPath, scriptPath.toString(), input.toString(), output.toString());

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


	static Path getRScriptResource(Script choice, Path outputDirectory) {

		String protocol = FileUtility.class.getResource("").getProtocol();
		Path tempResourceDirectory = null;

		switch (protocol) {
			case "file":
				Path rScriptsPath = Paths.get("src/main/resources/r_scripts");
				tempResourceDirectory = FileUtility.extractResourcesAsIde(outputDirectory, rScriptsPath);
				break;
			case "jar":
				try {
					File jarFile = new File(RInvoker
							.class
							.getProtectionDomain()
							.getCodeSource()
							.getLocation()
							.toURI());
					String resourceName = "r_scripts";
					tempResourceDirectory = FileUtility.extractResourcesAsJar(jarFile, outputDirectory, resourceName);
				}
				catch (URISyntaxException e) { e.printStackTrace(); }
				break;
			default:
				throw new RuntimeException("Protocol did not match with 'file' or 'jar'");
		}

		assert tempResourceDirectory != null;
		Path resource;

		switch (choice) {
			case AHP:
				resource = Paths.get(tempResourceDirectory.toString(), "r_scripts", "ahpWeightElicitation.R");
				break;
			case FAPH:
				resource = Paths.get(tempResourceDirectory.toString(), "r_scripts", "fahpWeightElicitator.R");
				break;
			case THRESHOLD:
				resource = Paths.get(tempResourceDirectory.toString(), "r_scripts", "thresholdsExtractor.R");
				break;
			default:
				throw new RuntimeException("Invalid choice enum given: [" + choice.name() + "]");
		}

		if (!resource.toFile().isFile()) throw new RuntimeException("getRScriptResource does not point to a existing " +
				"file at path " + resource.toString());

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

		// Assert r runner exists on file system at path
		if (!rPath.toFile().isFile()) {
			throw new RuntimeException("No R executable was found at path " + rPath.toAbsolutePath() +
					". Has R been installed on this machine?");
		}

		return rPath;
	}
}
