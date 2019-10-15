package qatch.analysis;

import java.util.HashSet;
import java.util.Set;

public class Diagnostic {

    String tool;
    String id;
    Set<Finding> findings = new HashSet<>();

    public Diagnostic(String tool, String id) {
        this.tool = tool;
        this.id = id;
    }

}
