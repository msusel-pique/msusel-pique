package pique.evaluation;

import pique.model.ModelNode;
import java.util.Collection;

// TODO (1.0): Documentation
public class DefaultNormalizer extends Normalizer {

    @Override
    public double normalize(double inValue, Collection<ModelNode> normalizers) {
        return inValue;
    }

}
