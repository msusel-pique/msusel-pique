package qatch.utility;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class FileUtility {

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
        if (name.isEmpty()) { throw new RuntimeException("An an assembly name must be provided"); }

        Path root = Paths.get(rootDirectory.toString());
        ArrayList<String> exts = new ArrayList<>(Arrays.asList(extensions));
        Set<Path> assemblyPaths = new HashSet<>();

        exts.forEach(ex -> {
            try {
                Files.find(root, Integer.MAX_VALUE, (path, attr) -> path.toString().endsWith(ex))
                        .filter(path -> path.endsWith(name + ex))
                        .forEach(assemblyPaths::add);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        if (assemblyPaths.isEmpty()) {
            throw new IllegalStateException("No assemblies with extension(s) " + Arrays.toString(extensions)
                + " were found for scanning. Has the project been compiled?");
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
    public static Set<String> findFileNamesFromExtension(Path rootDirectory, String extension) {

        Set<String> fileNames = new HashSet<>();

        try {
            Files.find(rootDirectory, Integer.MAX_VALUE, (path, attr) -> path.toString().endsWith(extension))
                    .forEach(p -> fileNames.add(FilenameUtils.getBaseName(p.toString())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileNames;
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
}