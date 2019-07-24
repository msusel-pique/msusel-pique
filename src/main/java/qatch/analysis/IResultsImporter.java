package qatch.analysis;

import qatch.model.IssueSet;
import java.io.File;

public interface IResultsImporter {

    // TODO: return generic vector to handle either IssueSet or MetricSet
    IssueSet parse(File path);

}
