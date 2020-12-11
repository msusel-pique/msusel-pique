package pique.evaluation;

import pique.model.Diagnostic;
import pique.model.ModelNode;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Collection;

// TODO (1.0): documentation
public class LoCNormalizer implements INormalizer {

//    @Override
//    public double normalize(double inValue, Diagnostic diagnosticNormalizer) {
//        if (diagnosticNormalizer.getValue() == 0.0) throw new RuntimeException("Diagnostic normalize value is 0.0. Throwing exception to avoid divide by 0 normalization");
//        return inValue / diagnosticNormalizer.getValue();
//    }

    @Override
    public double normalize(double inValue, Collection<ModelNode> normalizers) {
        throw new NotImplementedException();
    }

//    @Override
//    public String getNormalizerDiagnosticName() {
//        return "loc";
//    }

    @Override
    public String getName() {
        return "pique.evaluation.LoCNormalizer";
    }
}
