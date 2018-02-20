package model.configuration;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

public class XMLConfiguration implements Configuration {

    private String projectName;
    private String projectRootPath;
    private String projectTargetPath;
    private Document document;
    private List<Directory> directories;
    private String textContent;

    XMLConfiguration(Document document) {
        this.document = document;
        this.document.getDocumentElement().normalize();

        updateProjectProperties();
        updateDirectoryStructure();

        try {
            updateTextContent();
        } catch (TransformerException e) {
            e.printStackTrace(); //TODO improve exception
        }
    }

    private void updateTextContent() throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "no");
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));
        textContent = writer.toString();
    }

    private void updateDirectoryStructure() {
        Node rootDirsNode = document.getElementsByTagName("dirsRoot").item(0);
        Node dir = rootDirsNode.getFirstChild();
        List<Node> validNodes = new LinkedList<>();

        while(dir.getNextSibling()!= null){
            if (dir.getNodeType() == Node.ELEMENT_NODE) {
                validNodes.add(dir);
            }
            dir = dir.getNextSibling();
        }

        directories = XMLDirectoryFactory.create(validNodes);
    }

    private void updateProjectProperties() {
        //XML Schema ensures that only one config and dirsRoot node can exist
        Node configNode = document.getElementsByTagName("config").item(0);
        Element configElement = (Element) configNode;
        projectName = configElement.getElementsByTagName("project").item(0).getTextContent();
        projectRootPath = configElement.getElementsByTagName("root").item(0).getTextContent();
        projectTargetPath = configElement.getElementsByTagName("target").item(0).getTextContent();
    }

    @Override
    public String getProjectName() {
        return projectName;
    }

    @Override
    public String getProjectRootPath() {
        return projectRootPath;
    }

    @Override
    public String getProjectTargetPath() {
        return projectTargetPath;
    }

    @Override
    public List<Directory> getDirectories() {
        return directories;
    }

    @Override
    public String getTextContent() { return textContent; }

    @Override
    public String toString() {
        return projectName;
    }
}
