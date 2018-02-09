package model;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.LinkedList;
import java.util.List;

public class ConfigNode {

    private String name;
    private String prefix;
    private String separator;
    private ConfigNode parent;
    private List<ConfigNode> children = new LinkedList<>();

    public ConfigNode(Node node, ConfigNode parent) {

        this.parent = parent;

        Node child = node.getFirstChild();

        while(child.getNextSibling()!= null){
            if (child.getNodeType() == Node.ELEMENT_NODE) {

                if (child.getNodeName().equals("name")) {
                    name = child.getTextContent();
                } else if (child.getNodeName().equals("prefix")) {
                    prefix = child.getTextContent();
                } else if (child.getNodeName().equals("separator")) {
                    separator = child.getTextContent();
                } else if (child.getNodeName().equals("dirs")) {

                    Node dir = child.getFirstChild();

                    while (dir.getNextSibling() != null) {
                        if (dir.getNodeType() == Node.ELEMENT_NODE) {
                            children.add(new ConfigNode(dir, this));
                        }
                        dir = dir.getNextSibling();
                    }
                }
            }
            child = child.getNextSibling();
        }
    }

    public List<ConfigNode> getChildren() {
        return children;
    }

    public String getSeparator() {
        return separator != null ? separator : "";
    }

    public String getPrefix() {
        String parentPrefix = parent != null ? parent.getPrefix() + parent.getSeparator() : "";
        return prefix != null ? parentPrefix + prefix : "";
    }

    @Override
    public String toString() {
        String prefix = getPrefix().equals("") ? "" : getPrefix() + " ";
        return prefix + name;
    }
}
