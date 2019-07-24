package qatch.utility;

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
    private FileUtility() { }

    // (TODO): this method currently returns all directories containing a file with 'extensions' in it, but languages
    //         such as C# duplicate .EXEs in both the bin and obj folders, so findings end up duplicated. Consider an
    //         "industry standard" approach using standized configurations for each language regarding where to look
    //         for files/directories to allow the tool to scan
    public static Set<String> findAssemblyDirectories(File rootDirectory, String... extensions) {

        Path root = Paths.get(rootDirectory.toString());
        ArrayList<String> exts = new ArrayList<>(Arrays.asList(extensions));
        Set<String> assemblyPaths = new HashSet<>();

        exts.forEach(ex -> {
            try {
                Files.find(root, Integer.MAX_VALUE, (path, attr) -> path.toString().endsWith(ex))
                        .forEach(path -> assemblyPaths.add(path.getParent().toString()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return assemblyPaths;
    }
}