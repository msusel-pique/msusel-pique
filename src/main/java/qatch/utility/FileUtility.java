package qatch.utility;

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
     * a regex pattern
     *
     * @param rootDirectory
     *      Directory to begin recursive search at
     * @param name
     *      File name a found file needs to at least partly match with (optional)
     * @param extensions
     *      ex. ".exe", ".dll", "class" - The '.' is not necessary but is recommended
     * @return
     *
     */
    public static Set<String> findAssemblies(File rootDirectory, @Nullable String name, String... extensions) {

        // bad approach for handling functional null Name string because I need to graduate soon
        if (name.isEmpty()) { name = ""; }

        Path root = Paths.get(rootDirectory.toString());
        ArrayList<String> exts = new ArrayList<>(Arrays.asList(extensions));
        Set<String> assemblyPaths = new HashSet<>();

//        exts.forEach(ex -> {
//            try {
//                Files.find(root, Integer.MAX_VALUE, (path, attr) -> path.toString().endsWith(ex))
//                        .forEach(path -> assemblyPaths.add(path.getParent().toString()));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
        String finalName = name;
        exts.forEach(ex -> {
            try {
                Files.find(root, Integer.MAX_VALUE, (path, attr) -> path.toString().endsWith(ex))
                        .filter(path -> path.toString().contains(finalName))
                        .forEach(path -> assemblyPaths.add(path.getParent().toString()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });



        return assemblyPaths;
    }
}