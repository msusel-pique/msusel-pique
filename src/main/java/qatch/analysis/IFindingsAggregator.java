package qatch.analysis;

import qatch.evaluation.Project;

/**
 * Implementing classes define how to aggregate the issues (findings) produced by the
 * static analysis tool(s) on a single project
 */
public interface IFindingsAggregator {

    /**
     * This method is responsible for the aggregation of the issues of a single project.
     */
    void aggregate(Project project);
}
