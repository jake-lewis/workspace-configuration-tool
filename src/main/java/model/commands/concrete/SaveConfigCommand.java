package model.commands.concrete;

import model.commands.Command;

import java.io.File;

public class SaveConfigCommand implements Command {

    private File file;

    private SaveConfigCommand() {}

    public SaveConfigCommand(File file) {
        this.file = file;
    }

    public File getFile() {
        return this.file;
    }

    @Override
    public String getName() {
        return "Save configuration: " + file.getName();
    }
}
