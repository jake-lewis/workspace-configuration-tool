package model.configuration;

import model.Directory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.LinkedList;
import java.util.List;

public class XMLConfiguration implements Configuration {

    private String projectName;
    private String projectRootPath;
    private String projectTargetPath;
    private List<Directory> directories;

    XMLConfiguration(Document document) {
        document.getDocumentElement().normalize();

        //XML Schema ensures that only one config and dirsRoot node can exist
        Node configNode = document.getElementsByTagName("config").item(0);
        Node rootDirsNode = document.getElementsByTagName("dirsRoot").item(0);
        directories = new LinkedList<>();

        Element configElement = (Element) configNode;
        projectName = configElement.getElementsByTagName("project").item(0).getTextContent();
        projectRootPath = configElement.getElementsByTagName("root").item(0).getTextContent();
        projectTargetPath = configElement.getElementsByTagName("target").item(0).getTextContent();

        Node dir = rootDirsNode.getFirstChild();

        while(dir.getNextSibling()!= null){
            if (dir.getNodeType() == Node.ELEMENT_NODE) {
                directories.add(new Directory(dir, null));
            }
            dir = dir.getNextSibling();
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
    public String toString() {
        return projectName;
    }
}
