package model.configuration;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

public class XMLConfiguration implements Configuration {

    private String projectName;
    private String projectRootPath;
    private String projectTargetPath;
    private Document document;
    private List<Directory> directories;
    private String textContent;

    public XMLConfiguration(File file) throws IOException {
        try {
            InputStream stream = new FileInputStream(file);
            updateDocument(new InputSource(stream));
            stream.close();

            updateProjectProperties();
            updateDirectoryStructure();
            updateTextContent();
        } catch (IOException | SAXException | ParserConfigurationException | TransformerException e) {
            throw new IOException("An error occurred while parsing the file: " + file.getPath(), e);
        }
    }

    private void updateDocument(InputSource source) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        document = dBuilder.parse(source);
        document.getDocumentElement().normalize();
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
    public void setTextContent(String textContent) throws ParseException {
        try {
            updateDocument(new InputSource(new StringReader(textContent)));
            this.textContent = textContent;

            updateProjectProperties();
            updateDirectoryStructure();
        } catch (SAXParseException e) {
            throw new ParseException(e.getMessage(), e.getLineNumber());
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new IllegalArgumentException(e);
        }
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
