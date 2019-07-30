package qatch;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import qatch.utility.FileUtility;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

public class UtilityTests {

    @Test
    public void testFindAssemblies() throws IOException {

        FileUtils.forceMkdir(new File("./src/test/output"));
        File file1 = File.createTempFile("file1", ".ext1", new File("./src/test/output"));
        File file2 = File.createTempFile("file2", ".ext1", new File("./src/test/output"));
        File file3 = File.createTempFile("file3", ".ext2", new File("./src/test/output"));

        Set<Path> ext1 = FileUtility.findAssemblies(new File("./src"), file1.getName().substring(0, 3), ".ext1");
        Set<Path> ext2 = FileUtility.findAssemblies(new File("./src"), file1.getName().substring(0, 3), ".ext2");
        Set<Path> extAll = FileUtility.findAssemblies(new File("./src"), file1.getName().substring(0, 3), ".ext2", ".ext1");
        Set<Path> nullSubstring = FileUtility.findAssemblies(new File("./src"), "",  ".ext1");
        Set<Path> substring = FileUtility.findAssemblies(new File("./src"), "file1", ".ext1");
        Set<Path> fullSubstring = FileUtility.findAssemblies(new File("./src"), file2.getName(), ".ext1");

        Assert.assertTrue(ext1.contains(file1.toPath()));
        Assert.assertTrue(ext1.contains(file2.toPath()));
        Assert.assertFalse(ext1.contains(file3.toPath()));

        Assert.assertTrue(ext2.contains(file3.toPath()));
        Assert.assertFalse(ext2.contains(file1.toPath()));
        Assert.assertFalse(ext2.contains(file2.toPath()));

        Assert.assertTrue(extAll.contains(file1.toPath()));
        Assert.assertTrue(extAll.contains(file2.toPath()));
        Assert.assertTrue(extAll.contains(file3.toPath()));

        Assert.assertTrue(nullSubstring.contains(file1.toPath()));
        Assert.assertTrue(nullSubstring.contains(file2.toPath()));
        Assert.assertFalse(nullSubstring.contains(file3.toPath()));

        Assert.assertTrue(substring.contains(file1.toPath()));
        Assert.assertFalse(substring.contains(file2.toPath()));
        Assert.assertFalse(substring.contains(file3.toPath()));

        Assert.assertTrue(fullSubstring.contains(file2.toPath()));
        Assert.assertFalse(fullSubstring.contains(file1.toPath()));
        Assert.assertFalse(fullSubstring.contains(file3.toPath()));

        boolean isEmpty = false;
        try { Set<Path> noFoundExt = FileUtility.findAssemblies(new File("./src"), "", ".falseExt1", "falseExt2"); }
        catch (IllegalStateException e) { isEmpty = true; }
        finally { Assert.assertTrue(isEmpty); }

        file1.deleteOnExit();
        file2.deleteOnExit();
        file3.deleteOnExit();
    }

}
