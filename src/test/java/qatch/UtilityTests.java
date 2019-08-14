package qatch;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Assert;
import org.junit.Test;
import qatch.utility.FileUtility;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

public class UtilityTests {

    private File testOut = new File("./src/test/output");

    @Test
    public void testFindAssemblies() throws IOException {
        FileUtils.forceMkdir(testOut);
        File file1 = File.createTempFile("file1", ".ext1", new File("./src/test/output"));
        File file2 = File.createTempFile("file2", ".ext1", new File("./src/test/output"));
        File file3 = File.createTempFile("file3", ".ext2", new File("./src/test/output"));

        file1.deleteOnExit();
        file2.deleteOnExit();
        file3.deleteOnExit();

        Set<Path> f1 = FileUtility.findAssemblies(new File("./src"), FilenameUtils.getBaseName(file1.getName()), ".ext1");
        Set<Path> f2 = FileUtility.findAssemblies(new File("./src"), FilenameUtils.getBaseName(file2.getName()), ".ext1");
        Set<Path> f3 = FileUtility.findAssemblies(new File("./src"), FilenameUtils.getBaseName(file3.getName()), ".ext2");

        Assert.assertTrue(f1.contains(file1.toPath()));
        Assert.assertTrue(f2.contains(file2.toPath()));
        Assert.assertTrue(f3.contains(file3.toPath()));

        Assert.assertFalse(f1.contains(file2.toPath()));
        Assert.assertFalse(f1.contains(file3.toPath()));
        Assert.assertFalse(f3.contains(file1.toPath()));
    }

    @Test
    public void testFindFileNamesFromExtension() throws IOException {
        FileUtils.forceMkdir(testOut);
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
    }

    @Test
    public void testTempFileCopyFromJar() throws MalformedURLException {
        URL toCopy = this.getClass().getClassLoader().getResource("file1.txt");
        Path target = testOut.toPath();

        assert toCopy != null;
        File tempFile = FileUtility.tempFileCopyFromJar(toCopy, target);

        Assert.assertTrue(tempFile.exists());
        Assert.assertTrue(tempFile.isFile());
        Assert.assertTrue(tempFile.getName().contains("file1"));
        Assert.assertTrue(FilenameUtils.getExtension(tempFile.toString()).equalsIgnoreCase("txt"));
    }

    @Test
    public void testMultiProjectCollector() throws IOException {
        FileUtils.forceMkdir(testOut);
        File testDir = new File(testOut, "TMPC");
        TestHelper.clean(testDir);

        File testDir11 = new File(testDir, "11");
        File testDir12 = new File(testDir11, "12");
        File testDir21 = new File(testDir, "21");
        testDir11.mkdirs();
        testDir12.mkdirs();
        testDir21.mkdirs();

        File f11_10 = File.createTempFile("f11-10", ".ext1", testDir11);
        File f11_20 = File.createTempFile("f11-20", ".ext2", testDir11);
        File f12_10 = File.createTempFile("f11-10", ".ext1", testDir12);
        File f12_11 = File.createTempFile("f11-11", ".ext1", testDir12);
        File f21_20 = File.createTempFile("f11-20", ".ext2", testDir21);
        File f21_21 = File.createTempFile("f11-21", ".ext2", testDir21);
        f11_10.deleteOnExit();
        f11_20.deleteOnExit();
        f12_10.deleteOnExit();
        f12_11.deleteOnExit();
        f21_20.deleteOnExit();
        f21_21.deleteOnExit();

        Set<Path> ext1Paths = FileUtility.multiProjectCollector(testDir.toPath(), ".ext1");
        Set<Path> ext2Paths = FileUtility.multiProjectCollector(testDir.toPath(), ".ext2");

        Assert.assertTrue(ext1Paths.contains(f11_10.toPath().getParent().toAbsolutePath()));
        Assert.assertTrue(ext1Paths.contains(f12_10.toPath().getParent().toAbsolutePath()));
        Assert.assertTrue(ext1Paths.contains(f12_11.toPath().getParent().toAbsolutePath()));

        Assert.assertTrue(ext2Paths.contains(f11_20.toPath().getParent().toAbsolutePath()));
        Assert.assertTrue(ext2Paths.contains(f21_20.toPath().getParent().toAbsolutePath()));
        Assert.assertTrue(ext2Paths.contains(f21_21.toPath().getParent().toAbsolutePath()));
    }

}
