package model.configuration;

import model.Directory;

import java.util.Collections;
import java.util.List;

public class NullConfiguration implements Configuration {

    private static NullConfiguration INSTANCE = new NullConfiguration();

    private NullConfiguration() {}

    static NullConfiguration getInstance() {
        return INSTANCE;
    }

    @Override
    public String getProjectName() {
        return null;
    }

    @Override
    public String getProjectRootPath() {
        return null;
    }

    @Override
    public String getProjectTargetPath() {
        return null;
    }

    @Override
    public List<Directory> getDirectories() {
        return Collections.emptyList();
    }

    @Override
    public boolean equals(Object obj) {
        return obj == null;
    }
}
