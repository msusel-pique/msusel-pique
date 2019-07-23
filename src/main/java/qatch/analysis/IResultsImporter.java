package qatch.analysis;

import qatch.model.IssueSet;

public interface IResultsImporter {

    IssueSet parseIssues(String path);

}
