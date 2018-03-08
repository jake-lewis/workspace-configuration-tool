package model.strategies;

import model.configuration.Directory;

import java.util.LinkedList;
import java.util.List;

public class ApplyToRoot extends ConfigApplicator {
    public ApplyToRoot(String rootPath, String targetPath) {
        super(rootPath, targetPath);
    }

    @Override
    public List<Directory> apply() {
        return new LinkedList<>();
    }
}
