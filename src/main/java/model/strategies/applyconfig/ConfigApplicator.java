package model.strategies.applyconfig;

import model.configuration.Directory;
import model.configuration.InvalidConfigurationException;

import java.io.File;
import java.util.List;

public abstract class ConfigApplicator {

    private File rootFolder, targetFolder;

    public ConfigApplicator(String rootPath, String targetPath) {
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
