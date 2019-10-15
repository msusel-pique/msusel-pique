package qatch.analysis;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ToolTests {

    public class ToolInstance extends Tool {
        ToolInstance(String name, Path toolConfig) { super(name, toolConfig); }

        @Override
        public Path analyze(Path projectLocation) { return null; }

        @Override
        public AnalylsisResult parse(Path toolResults) { return null; }
    }

    @Test
    public void testMapMeasure() {

        ITool t = new ToolInstance("Test Tool", Paths.get("src/test/resources/config/roslynatormeasures.yaml"));

        System.out.println("...");
    }
}
