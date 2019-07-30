package qatch;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import qatch.utility.FileUtility;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

public class UtilityTests {

    @Test
    public void testFindAssemblies() throws IOException {

        FileUtils.forceMkdir(new File("./src/test/output"));
        File file1 = File.createTempFile("file1", ".ext1", new File("./src/test/output"));
        File file2 = File.createTempFile("file2", ".ext1", new File("./src/test/output"));
        File file3 = File.createTempFile("file3", ".ext2", new File("./src/test/output"));

        file1.deleteOnExit();
        file2.deleteOnExit();
        file3.deleteOnExit();

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


    }

    @Test
    public void testFindFileNamesFromExtension() throws IOException {

        FileUtils.forceMkdir(new File("./src/test/output"));
        File file1 = File.createTempFile("file1", ".ext1", new File("./src/test/output"));
        File file2 = File.createTempFile("file2", ".ext1", new File("./src/test/output"));
        File file3 = File.createTempFile("file3", ".ext2", new File("./src/test/output"));
        File file4 = File.createTempFile("file4", ".ext2.ext3", new File("./src/test/output"));
        File file5 = File.createTempFile("file5", "", new File("./src/test/output"));

        file1.deleteOnExit();
        file2.deleteOnExit();
        file3.deleteOnExit();
        file4.deleteOnExit();
        file5.deleteOnExit();

        Set<String> namesExt1 = FileUtility.findFileNamesFromExtension(Paths.get("./src"), ".ext1");
        Set<String> namesExt2 = FileUtility.findFileNamesFromExtension(Paths.get("./src"), ".ext2");
        Set<String> namesExt3 = FileUtility.findFileNamesFromExtension(Paths.get("./src"), ".ext3");

        System.out.println(file1.getName());

        String file1Name = file1.getName();
        String file2Name = file2.getName();
        String file3Name = file3.getName();
        String file4Name = file4.getName();
        String file5Name = file5.getName();

        String substring = file3Name.substring(0, file3Name.length() - 5);
        String substring1 = file4Name.substring(0, file4Name.length() - 5);

        Assert.assertTrue(namesExt1.contains(file1Name.substring(0, file1Name.length()-5)));
        Assert.assertTrue(namesExt1.contains(file2Name.substring(0, file2Name.length()-5)));
        Assert.assertFalse(namesExt1.contains(substring));
        Assert.assertFalse(namesExt1.contains(substring1));
        Assert.assertFalse(namesExt1.contains(file5Name.substring(0, file5Name.length()-5)));
        Assert.assertTrue(namesExt2.contains(substring));
        Assert.assertTrue(namesExt3.contains(substring1));

        System.out.println("waiting...");
    }

}
