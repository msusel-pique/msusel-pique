package qatch.analysis;

import org.xml.sax.SAXException;
import qatch.model.IssueSet;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public interface IFindingsResultsImporter {

    IssueSet parse(String path) throws ParserConfigurationException, IOException, SAXException;

}
