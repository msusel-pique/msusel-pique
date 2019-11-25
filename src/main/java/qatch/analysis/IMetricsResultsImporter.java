package qatch.analysis;

import qatch.model.MetricSet;
import java.io.IOException;
import java.nio.file.Path;

@Deprecated
public interface IMetricsResultsImporter {
    MetricSet parse(Path path) throws IOException;
}
