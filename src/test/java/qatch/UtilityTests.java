package qatch;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import qatch.utility.FileUtility;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class UtilityTests {

    @Test
    public void testFindAssemblies() throws IOException {

        FileUtils.forceMkdir(new File("./src/test/output"));
        File file1 = File.createTempFile("file1", ".ext1", new File("./src/test/output"));
        File file2 = File.createTempFile("file2", ".ext1", new File("./src/test/output"));
        File file3 = File.createTempFile("file3", ".ext2", new File("./src/test/output"));

        Set<String> assemblies = FileUtility.findAssemblies(new File("./src/test/output"), file1.getName().substring(2, 5), ".ext1");

        file1.deleteOnExit();
        file2.deleteOnExit();
        file3.deleteOnExit();

        System.out.println("waiting...");
    }

}
