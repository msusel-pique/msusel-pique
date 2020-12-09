package pique.evaluation;

import pique.model.Diagnostic;
import pique.model.ModelNode;

import java.util.Collection;

// TODO (1.0): Documentation
public interface INormalizer {

    double normalize(double inValue, Collection<ModelNode> normalizers);

    String getNormalizerName();
}
