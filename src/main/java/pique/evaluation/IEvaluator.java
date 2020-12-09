package pique.evaluation;

// TODO (1.0): documentation
public interface IEvaluator {

    /**
     * @return
     *      The evaluated value of a node after running evalation given that node's state
     */
    double evalStrategy();

    /**
     * @return
     *      The human-readable name of this evaluator
     */
    String getName();

}
