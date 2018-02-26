package model.configuration;

import org.w3c.dom.Node;

import java.util.LinkedList;
import java.util.List;

public class XMLDirectoryFactory {
    public static List<Directory> create(List<Node> nodes) throws InvalidConfigurationException {

        List<Directory> result = new LinkedList<>();

        for (Node node : nodes) {
            result.add(XMLDirectoryFactory.create(node, null));
        }

        return result;
    }

    public static Directory create(Node node, Directory parent) throws InvalidConfigurationException {
        Node child = node.getFirstChild();
        Directory directory = new Directory(parent);
        List<Directory> children = new LinkedList<>();

        while(child != null) {
            switch (child.getNodeName()) {
                case "name":
                    directory.setName(child.getTextContent());
                    break;
                case "prefix":
                    directory.setPrefix(child.getTextContent());
                    break;
                case "separator":
                    directory.setSeparator(child.getTextContent());
                    break;
                case "dirs":
                    Node subNode = child.getFirstChild();

                    while (subNode != null) {
                        if (subNode.getNodeType() == Node.ELEMENT_NODE) {
                            children.add(XMLDirectoryFactory.create(subNode, directory));
                        }
                        subNode = subNode.getNextSibling();
                    }
                    directory.setChildren(children);
                    break;
            }
            child = child.getNextSibling();
        }

        if (directory.getName() == null) {
            throw new InvalidConfigurationException("A <dir> tag is missing it's <name> element.");
        }

        return directory;
    }
}
