package model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.LinkedList;
import java.util.List;

public class Configuration {

    private String projectName;
    private String projectRootPath;
    private String projectTargetPath;
    private List<ConfigNode> configNodes;

    public Configuration(Document document) {
        document.getDocumentElement().normalize();

        //XML Schema ensures that only one config and dirsRoot node can exist
        Node configNode = document.getElementsByTagName("config").item(0);
        Node rootDirsNode = document.getElementsByTagName("dirsRoot").item(0);
        configNodes = new LinkedList<>();

        Element configElement = (Element) configNode;
        projectName = configElement.getElementsByTagName("project").item(0).getTextContent();
        projectRootPath = configElement.getElementsByTagName("root").item(0).getTextContent();
        projectTargetPath = configElement.getElementsByTagName("target").item(0).getTextContent();

        Node dir = rootDirsNode.getFirstChild();

        while(dir.getNextSibling()!= null){
            if (dir.getNodeType() == Node.ELEMENT_NODE) {
                configNodes.add(new ConfigNode(dir, null));
            }
            dir = dir.getNextSibling();
        }
    }
}
