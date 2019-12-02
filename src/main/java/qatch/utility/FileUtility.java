package qatch.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * A collection of useful static methods for common file procedures used in Qatch operation
 */
public class FileUtility {

    /**
     * Create a hard-drive file representation of the model
     *
     * @param outputDirectory
     * 		The directory to place the json file into.  Does not need to exist beforehand.
     * @param fileName
     *      What to name the file.  Should not inclue '.json' suffix.
     * @return
     * 		The path of the exported object as a json file.
     */
    public static Path exportObjectToJson(Object object, Path outputDirectory, String fileName) {

        // ensure target path directory exists
        outputDirectory.toFile().mkdirs();

        // instantiate results file reference
        File fileOut = new File(outputDirectory.toFile(),fileName.replaceAll("\\s","") + ".json");

        //Instantiate a Json Parser
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        //Create the Json String of the projects
        String json = gson.toJson(object);

        //Save the results
        try {
            FileWriter writer = new FileWriter(fileOut.toString());
            writer.write(json);
            writer.close();
        } catch(IOException e){
            System.out.println(e.getMessage());
        }

        return fileOut.toPath();
    }

    /**
     * Extract all resources found in this project (msusel-qatch) to a temporary file-system directory.
     *
     * @param destination
     *      Location to create the temporary directory. Does not need to initially exist.
     * @param directoryNameToExtract
     *      Name of jar resource directory containing the files desired to be extracted
     * @return
     *      The temporary file-system directory with resources extracted into it.
     */
    public static Path extractResources(Path destination, String directoryNameToExtract)  {

        destination.toFile().mkdirs();
        String protocol = FileUtility.class.getResource("").getProtocol();

        try {
            Path resourcesDirectory = Files.createTempDirectory(destination, "resources");
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try { FileUtils.deleteDirectory(resourcesDirectory.toFile()); }
                catch (IOException e) { e.printStackTrace(); }
            }));

            if (Objects.equals(protocol, "jar")) {
                try { extractResourcesToTempFolder(resourcesDirectory, directoryNameToExtract); }
                catch (IOException | URISyntaxException e) { e.printStackTrace(); }
            }

            else if (Objects.equals(protocol, "file")) {
                File rScripts = new File("src/main/resources", directoryNameToExtract);
                try {
                    FileUtils.copyDirectoryToDirectory(rScripts , resourcesDirectory.toFile());
                }
                catch (IOException e) {  e.printStackTrace(); }
            }

            else { throw new RuntimeException("Unable to determine if project is running from IDE or JAR"); }

            return resourcesDirectory;

        } catch (IOException e) {
            e.printStackTrace();
        }

        throw new RuntimeException("retrun statement in try block was never reached.");
    }

    /**
     * Code from https://stackoverflow.com/questions/1529611/how-to-write-a-java-program-which-can-extract-a-jar-file-and-store-its-data-in-s/1529707#1529707
     * by user Jamesst20
     *
     * Used when running program as a JAR.
     * Takes resources in the resources folder within the JAR and copies them to a
     * resources folder in the same directory as the JAR.
     *
     * @param destination
     *      Location (directory) to extract the resource files to
     * @param directoryName
     *      Name of directory containing the files desired to be extracted
     */
    public static void extractResourcesToTempFolder(Path destination, String directoryName) throws IOException, URISyntaxException {
        File jarFile = new File(FileUtility
                .class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toURI());

        //Recursively build resources folder from JAR sibling to JAR file
        JarFile jar = new JarFile(jarFile.getPath());
        Enumeration<JarEntry> enums = jar.entries();
        while (enums.hasMoreElements()) {
            JarEntry entry = enums.nextElement();
            if (entry.getName().startsWith(directoryName)) {
                File toWrite = new File(destination.toFile(), entry.getName());
                if (entry.isDirectory()) {
                    toWrite.mkdirs();
                    continue;
                }
                InputStream in = new BufferedInputStream(jar.getInputStream(entry));
                OutputStream out = new BufferedOutputStream(new FileOutputStream(toWrite));
                byte[] buffer = new byte[2048];
                for (;;) {
                    int nBytes = in.read(buffer);
                    if (nBytes <= 0) {
                        break;
                    }
                    out.write(buffer, 0, nBytes);
                }
                out.flush();
                out.close();
                in.close();
            }
        }
    }

    /**
     * Recursively find all files under a root directory with specified extension(s) and optionally with name matching
     * a substring pattern
     *
     * @param rootDirectory
     *      Directory to begin recursive search at
     * @param name
     *      File name a found path must end with. To find src\Logging\bin\Debug\EF.Azure.dll, name should be 'EF.Azure'
     * @param extensions
     *      ex. ".exe", ".dll", ".class" - The '.' is necessary if a name to filter with is also provided
     * @return
     *      The set of paths containing matched files
     */
    public static Set<Path> findAssemblies(File rootDirectory, String name, String... extensions) throws IllegalStateException {

        // bad approach for handling functional null Name string because I need to graduate soon
        if (name.isEmpty()) { throw new RuntimeException("An assembly name must be provided"); }

        Path root = Paths.get(rootDirectory.toString());
        ArrayList<String> exts = new ArrayList<>(Arrays.asList(extensions));
        Set<Path> assemblyPaths = new HashSet<>();

        exts.forEach(ex -> {
            try {
                Files.find(root, Integer.MAX_VALUE, (path, attr) -> path.toString().endsWith(ex))
                        .filter(path -> path.endsWith(name + ex))
                        .forEach(assemblyPaths::add);
            }
            catch (IOException e) { e.printStackTrace(); }
        });

        if (assemblyPaths.isEmpty()) {
            // TODO: output warning using logger class
            System.out.println("[Warning] No assemblies with extension(s) " + Arrays.toString(extensions) +
                    " in directory\n\t" + rootDirectory.toString() +
                    "\n\twere found for scanning. Has the project been compiled?");
        }

        return assemblyPaths;
    }

    /**
     * Collects set of file names that match a given extension
     *
     * @param rootDirectory
     *      Directory to begin recursive search at
     * @param extension
     *      ".exe", ".dll", "class" - The '.' is not necessary but is recommended
     * @return
     *      Set of file names without the extension and path (e.g. {WpfApp1, Roslyn} if the root directory
     *      contained files named src/bin/WpfApp1.exe and src/obj/Roslyn.exe
     */
    public static Set<String> findFileNamesFromExtension(Path rootDirectory, String extension, Integer depth) {

        Set<String> fileNames = new HashSet<>();

        try {
            Files.find(rootDirectory, depth, (path, attr) -> path.toString().endsWith(extension))
                    .forEach(p -> fileNames.add(FilenameUtils.getBaseName(p.toString())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileNames;
    }


    /**
     * @return
     *      Path of working root directory of the project calling this method
     */
    public static Path getRoot() {
        return Paths.get(System.getProperty("user.dir"));
    }

    /**
     * Collect set of directory file paths belonging to individual project or module roots when starting
     * from a repository directory containing multiple projects or modules
     * @param root
     *      the path to the repository directory that holds many individual projects
     * @param flagSuffix
     *      a file extension that signifies a single project is contained in the same directory.
     *      e.g. ".csproj" for C# modules or "pom.xml" for java maven projects
     * @return
     *      the set of individual project root paths
     */
    public static Set<Path> multiProjectCollector(Path root, String flagSuffix) {

        Set<Path> projectPaths = new HashSet<>();
        try {
            Files.find(root, Integer.MAX_VALUE, (path, attr) -> path.toString().toLowerCase().endsWith(flagSuffix.toLowerCase()))
                    .forEach(p -> projectPaths.add(p.getParent().toAbsolutePath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return projectPaths;
    }

    /**
     * Create a temporary disk copy of any file a URL can point to.
     * This is useful for resources contained in a JAR or ZIP that cannot be utilized
     * as a stream (such as for use by external tools).
     *
     * @param toCopy
     *      The single file to make a temporary copy of. Must point to a file, not a directory
     * @param target
     *      The location to place the temporary file. Must be a directory
     * @return
     *      The temporary file
     */
    public static File tempFileCopyFromJar(URL toCopy, Path target) {

        File file = null;
        try {
            InputStream input = toCopy.openStream();
            file = File.createTempFile(
                        FilenameUtils.getBaseName(toCopy.getPath()),
                        "." + FilenameUtils.getExtension(toCopy.getPath()),
                        target.toFile()
                    );
            OutputStream out = new FileOutputStream(file);
            int read;
            byte[] bytes = new byte[1024];
            while ((read = input.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.close();
            file.deleteOnExit();
        }
        catch (IOException e) { e.printStackTrace(); }
        if (file != null && !file.exists()) {
            throw new RuntimeException("Error: File " + file + " not found!");
        }

        return file;
    }
}