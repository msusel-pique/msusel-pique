package qatch.analysis;

import org.xml.sax.SAXException;
import qatch.model.IssueSet;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;

public interface IFindingsResultsImporter {

    IssueSet parse(Path path) throws ParserConfigurationException, IOException, SAXException;

}
