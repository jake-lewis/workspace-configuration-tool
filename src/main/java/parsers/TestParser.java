package parsers;

import model.Configuration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class TestParser {
    private Document document;

    public void test() {

        try {
            File inputFile = new File("src/main/resources/example config/exampleConfig.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);

            Configuration config = new Configuration(doc);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
