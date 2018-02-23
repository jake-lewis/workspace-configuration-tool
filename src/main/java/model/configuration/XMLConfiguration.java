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
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
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

    private XMLConfiguration() {};

    public XMLConfiguration(File file) throws IOException, InvalidConfigurationException {
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

    public XMLConfiguration(String text) throws ParseException, InvalidConfigurationException, ParserConfigurationException, TransformerException, SAXException, IOException {
        try {
            Reader reader = new StringReader(text);
            updateDocument(new InputSource(reader));
            reader.close();

            updateProjectProperties();
            updateDirectoryStructure();
            updateTextContent();
        } catch (SAXParseException e) {
            throw new ParseException(e.getMessage(), e.getLineNumber());
        } catch (IOException | SAXException | ParserConfigurationException | TransformerException e) {
            //throw new Exception("An error occurred while parsing the text", e);
            throw e;
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

    private void updateDirectoryStructure() throws InvalidConfigurationException {
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

    private void updateProjectProperties() throws InvalidConfigurationException {
        //XML Schema ensures that only one config and dirsRoot node can exist
        Node configNode = document.getElementsByTagName("config").item(0);
        Element configElement = (Element) configNode;
        try {
            projectName = configElement.getElementsByTagName("project").item(0).getTextContent();
            projectRootPath = configElement.getElementsByTagName("root").item(0).getTextContent();
            projectTargetPath = configElement.getElementsByTagName("target").item(0).getTextContent();
        } catch (NullPointerException e) {
            throw new InvalidConfigurationException("The configuration's <config> tag is incomplete");
        }
    }

    @Override
    public void setTextContent(String textContent) throws ParseException, InvalidConfigurationException {
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

    public void setProjectName(String projectName) {
        Node configNode = document.getElementsByTagName("config").item(0);
        Element configElement = (Element) configNode;
        configElement.getElementsByTagName("project").item(0).setTextContent(projectName);
        this.projectName = projectName;
    }

    public void setProjectRootPath(String projectRootPath) {
        Node configNode = document.getElementsByTagName("config").item(0);
        Element configElement = (Element) configNode;
        configElement.getElementsByTagName("root").item(0).setTextContent(projectRootPath);
        this.projectRootPath = projectRootPath;
    }

    public void setProjectTargetPath(String projectTargetPath) {
        Node configNode = document.getElementsByTagName("config").item(0);
        Element configElement = (Element) configNode;
        configElement.getElementsByTagName("target").item(0).setTextContent(projectTargetPath);
        this.projectTargetPath = projectTargetPath;
    }

    public void setDirectories(List<Directory> directories) {

        //TODO could be more efficient if it only redoes changed nodes
        //Clear existing directories in document
        Node rootDirsNode = document.getElementsByTagName("dirsRoot").item(0);
        while (rootDirsNode.hasChildNodes()) {
            rootDirsNode.removeChild(rootDirsNode.getFirstChild());
        }

        //Generate new directories
        List<Node> dirNodes = createNodes(directories);
        for (Node dir : dirNodes) {
            rootDirsNode.appendChild(dir);
        }
    }

    private List<Node> createNodes(List<Directory> directories) {
        List<Node> dirNodes = new LinkedList<>();

        for (Directory directory : directories) {
            Element dir = document.createElement("dir");
            if (!directory.getDirectPrefix().isEmpty()) {
                Element prefix = document.createElement("prefix");
                prefix.setTextContent(directory.getDirectPrefix());
                dir.appendChild(prefix);
            }
            if (!directory.getSeparator().isEmpty()) {
                Element separator = document.createElement("separator");
                separator.setTextContent(directory.getSeparator());
                dir.appendChild(separator);
            }
            Element name = document.createElement("name");
            name.setTextContent(directory.getName());
            dir.appendChild(name);

            if (!directory.getChildren().isEmpty()) {
                Element dirs = document.createElement("dirs");

                List<Node> subDirs = createNodes(directory.getChildren());
                for (Node subDir : subDirs) {
                    dirs.appendChild(subDir);
                }

                dir.appendChild(dirs);
            }
            dirNodes.add(dir);
        }

        return dirNodes;
    }

    public static XMLConfiguration copy(XMLConfiguration configuration) throws InvalidConfigurationException {
        XMLConfiguration newConfig = new XMLConfiguration();
        newConfig.document = configuration.document;
        try {
            newConfig.updateProjectProperties();
            newConfig.updateDirectoryStructure();
            newConfig.updateTextContent();
            return newConfig;
        } catch (InvalidConfigurationException | TransformerException e) {
            throw new InvalidConfigurationException("Error creating copy of configuration. " +
                    "Original configuration is in an invalid state.", e);
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
