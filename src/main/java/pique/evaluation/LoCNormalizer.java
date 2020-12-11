package pique.evaluation;

import pique.model.ModelNode;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Collection;

// TODO (1.0): documentation
public class LoCNormalizer implements INormalizer {

    @Override
    public double normalize(double inValue, Collection<ModelNode> normalizers) {

        if (normalizers.size() != 1)
            throw new IllegalArgumentException("LoC normalizer expects a collection of exactly one normalizer.");

        // TODO: Pickup here. Run testProjectConstruction().  Need to initialize a diagnostic with name "loc" for
        //  defaultnormalizer to work.
        double linesOfCode = normalizers.stream()
                .findFirst()
                .get()
                .getValue();

        if (linesOfCode < 1) throw new IllegalStateException("Lines of code was not 1 or larger");

        return inValue / linesOfCode;
    }


    @Override
    public String getName() {
        return "pique.evaluation.LoCNormalizer";
    }
}
