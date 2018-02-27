package model.configuration;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class Directory {

    private String name;
    private String prefix;
    private String separator;
    private Directory parent;
    private List<Directory> children = new LinkedList<>();

    public Directory(Directory parent) {
        this(null, null, null, parent);
    }

    public Directory(String name, String prefix, String separator, Directory parent) {
        this.name = name;
        this.prefix = prefix;
        this.separator = separator;
        this.parent = parent;
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

    public void setChildren(List<Directory> children) {
        this.children = children;
    }

    public void setName(String name) throws InvalidConfigurationException {
        if (name == null || name.isEmpty()) {
            throw new InvalidConfigurationException("Directory names must not be Null or empty");
        }
        this.name = name;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }
}
