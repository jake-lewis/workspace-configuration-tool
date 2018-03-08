package model.strategies;

import model.configuration.Directory;
import model.configuration.InvalidConfigurationException;

import java.io.File;
import java.util.List;

public abstract class ConfigApplicator {

    private String rootPath, targetPath;
    private File rootFolder, targetFolder;

    public ConfigApplicator(String rootPath, String targetPath) {
        this.rootPath = rootPath;
        this.targetPath = targetPath;
        this.rootFolder = new File(rootPath);
        this.targetFolder = new File(targetPath);
    }

    public abstract List<Directory> apply() throws InvalidConfigurationException;

    public File getRootFolder() {
        return rootFolder;
    }

    public File getTargetFolder() {
        return targetFolder;
    }
}
