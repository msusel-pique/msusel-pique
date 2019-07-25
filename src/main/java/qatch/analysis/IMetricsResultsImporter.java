package qatch.analysis;

import qatch.model.MetricSet;
import java.io.IOException;

public interface IMetricsResultsImporter {
    MetricSet parse(String path) throws IOException;
}
