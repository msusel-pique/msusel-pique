package qatch.runnable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;

public class SingleProjectEvaluatorTests {

    private SingleProjectEvaluator spe;

    @Before
    public void initClass() {
        spe = new SingleProjectEvaluator();
    }

    @Test
    public void testInitialize() {
        try {
            spe.initialize(
                    Paths.get("src/test/resources/TestCsharpProject"),
                    Paths.get("src/test/output/SingleProjEval"),
                    Paths.get("src/test/resources/qualityModel_iso25k_csharp.xml"),
                    Paths.get("src/test/resources/fake_tools"));
        }
        catch (IllegalArgumentException e) { Assert.fail(e.getMessage()); }

        try {
            spe.initialize(
                    Paths.get("src/test/resources/IDONTEXIST"),
                    Paths.get("src/test/output/SingleProjEval"),
                    Paths.get("src/test/resources/qualityModel_iso25k_csharp.xml"),
                    Paths.get("src/test/resources/fake_tools"));
        }
        catch (IllegalArgumentException ignored) {  }

    }
}
