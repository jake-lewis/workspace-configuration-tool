package model.configuration;

import org.w3c.dom.Node;

import java.util.LinkedList;
import java.util.List;

public class Directory {

    private String name;
    private String prefix;
    private String separator;
    private Directory parent;
    private List<Directory> children = new LinkedList<>();

    public Directory(Node node, Directory parent) {

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
                            children.add(new Directory(dir, this));
                        }
                        dir = dir.getNextSibling();
                    }
                }
            }
            child = child.getNextSibling();
        }
    }

    public List<Directory> getChildren() {
        return children;
    }

    public String getName() { return name; }

    public String getSeparator() {
        return separator != null ? separator : "";
    }

    public String getDirectPrefix() {
        return prefix;
    }

    public String getFullPrefix() {
        String parentPrefix = parent != null ? parent.getFullPrefix() + parent.getSeparator() : "";
        return prefix != null ? parentPrefix + prefix : "";
    }

    @Override
    public String toString() {
        String prefix = getFullPrefix().equals("") ? "" : getFullPrefix() + " ";
        return prefix + name;
    }
}
