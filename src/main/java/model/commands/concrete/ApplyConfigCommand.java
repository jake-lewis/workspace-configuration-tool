package model.commands.concrete;

import model.commands.Command;
import model.strategies.applyconfig.ConfigApplicator;

import java.io.File;

public abstract class ApplyConfigCommand implements Command {

    private File sourceFolder, targetFolder;
    private ConfigApplicator applicator;

    public ApplyConfigCommand(ConfigApplicator applicator) {
        this.applicator = applicator;
        sourceFolder = applicator.getRootFolder();
        targetFolder = applicator.getTargetFolder();
    }

    public File getSourceFolder() {
        return sourceFolder;
    }

    public File getTargetFolder() {
        return targetFolder;
    }

    public ConfigApplicator getApplicator() {
        return applicator;
    }

    @Override
    public abstract String getName();
}