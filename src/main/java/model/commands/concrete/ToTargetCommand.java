package model.commands.concrete;

import model.commands.Command;

import java.io.File;

public class ToTargetCommand implements Command {

    private File sourceFolder, targetFolder;

    public ToTargetCommand(String sourcePath, String targetPath) {
        sourceFolder = new File(sourcePath);
        targetFolder = new File(targetPath);
    }

    public File getSourceFolder() {
        return sourceFolder;
    }

    public File getTargetFolder() {
        return targetFolder;
    }

    @Override
    public String getName() {
        return null;
    }
}
