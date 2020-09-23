package pique.evaluation;

import pique.model.Diagnostic;

public interface INormalizer {

    double normalize(double inValue, Diagnostic diagnosticNormalizer);

    String getNormalizerDiagnosticName();

    String getNormalizerName();
}
