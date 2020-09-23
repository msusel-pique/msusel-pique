package pique.evaluation;

import pique.model.Diagnostic;

public class DefaultNormalizer implements INormalizer {


    @Override
    public double normalize(double inValue, Diagnostic diagnosticNormalizer) {
        if (diagnosticNormalizer.getValue() == 0.0) throw new RuntimeException("Diagnostic normalize value is 0.0. Throwing exception to avoid divide by 0 normalization");
        return inValue / diagnosticNormalizer.getValue();
    }

    @Override
    public String getNormalizerDiagnosticName() {
        return "loc";
    }

    @Override
    public String getNormalizerName() {
        return "pique.evaluation.DefaultNormalizer";
    }
}
