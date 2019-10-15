package qatch.analysis;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ToolTests {

    public class ToolInstance extends Tool {
        public ToolInstance() { super("", Paths.get("")); }

        @Override
        public Path analyze(Path projectLocation) { return null; }

        @Override
        public AnalylsisResult parse(Path toolResults) { return null; }
    }

    @Test
    public void testMapMeasure() {

        System.out.println("...");
    }
}
