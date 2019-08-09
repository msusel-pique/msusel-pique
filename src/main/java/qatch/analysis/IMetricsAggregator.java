package qatch.analysis;

import qatch.evaluation.Project;

/**
 * Implementing classes calculate the value of the properties that are quantified
 * by previously run metrics static analysis tool(s).
 *
 * At a minimum, this should be used to calculated the weighted sum of each metric
 * against all the classes of the project using LOC as the weight.
 */
public interface IMetricsAggregator {

    /**
     * The method that implements the whole functionality of this class.
     */
    void aggregate(Project project);
}
