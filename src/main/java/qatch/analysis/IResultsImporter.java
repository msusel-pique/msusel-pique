package qatch.analysis;

import qatch.model.IssueSet;
import java.io.File;

public interface IResultsImporter {

    IssueSet parseIssues(File path);

}
