package qatch.utility;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

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
     *      File name a found file needs to at least partly match with. "" matches everything.
     * @param extensions
     *      ex. ".exe", ".dll", "class" - The '.' is not necessary but is recommended
     * @return
     *      The set of paths containing matched files
     */
    public static Set<Path> findAssemblies(File rootDirectory, String name, @NotNull String... extensions) throws IllegalStateException {

        // bad approach for handling functional null Name string because I need to graduate soon
        if (name.isEmpty()) { name = ""; }

        Path root = Paths.get(rootDirectory.toString());
        ArrayList<String> exts = new ArrayList<>(Arrays.asList(extensions));
        Set<Path> assemblyPaths = new HashSet<>();

        String finalName = name;
        exts.forEach(ex -> {
            try {
                Files.find(root, Integer.MAX_VALUE, (path, attr) -> path.toString().endsWith(ex))
                        .filter(path -> path.toString().contains(finalName))
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
}