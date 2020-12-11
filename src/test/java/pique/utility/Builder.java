package pique.utility;

import pique.evaluation.DefaultDiagnosticEvaluator;
import pique.evaluation.LoCNormalizer;
import pique.evaluation.Project;
import pique.model.*;

/**
 * Test utility class to build "as simple as possible" PIQUE object instances
 */
public class Builder {

    //region ModelNode objects

    public static Finding buildFinding(String filePath, int lineNumber, int severity) {
        return new Finding(filePath, lineNumber, 11, severity);
    }

    public static Diagnostic buildDiagnostic(String id) {
        Diagnostic d = new Diagnostic(id, "Sample Description", "Sample Tool Name");

        Finding f1 = buildFinding(id + "/file/path", 1, 1);
        Finding f2 = buildFinding(id + "/file/path", 2, 2);
        Finding f3 = buildFinding(id + "/file/path", 3, 2);

        d.setChild(f1);
        d.setChild(f2);
        d.setChild(f3);

        return d;
    }

    public static Measure buildMeasure(String name) {
        Measure measure = new Measure(name, name + " description", new LoCNormalizer(), false);

        Diagnostic d1 = buildDiagnostic(name + "_Diagnostic 01");
        Diagnostic d2 = buildDiagnostic(name + "_Diagnostic 02");

        measure.setChild(d1);
        measure.setChild(d2);

        return measure;
    }

    public static ProductFactor buildProductFactor(String name) {
        ProductFactor pf = new ProductFactor(name, name + " description");
        Measure m1 = buildMeasure(name + "_Measure 01");
        pf.setChild(m1);

        return pf;
    }

    public static QualityAspect buildQualityAspect(String name) {
        QualityAspect qa = new QualityAspect(name, name + " description");

        ProductFactor pf1 = buildProductFactor(name + "_ProductFactor 01");
        ProductFactor pf2 = buildProductFactor(name + "_ProductFactor 02");

        qa.setChild(pf1);
        qa.setChild(pf2);

        return qa;
    }

    public static Tqi buildTqi(String name) {
        Tqi tqi = new Tqi(name, name + " description", null);

        QualityAspect qa1 = buildQualityAspect("QA01");
        QualityAspect qa2 = buildQualityAspect("QA02");

        tqi.setChild(qa1);
        tqi.setChild(qa2);

        return tqi;
    }

    //endregion

    public static QualityModel buildQualityModel(String name) {
        Tqi tqi = buildTqi("Total Quality");
        return new QualityModel(name, tqi);
    }

    public static Project buildProject(String name) {
        QualityModel qm = buildQualityModel("Test QM");
        return new Project("Test Project", qm);
    }
}
