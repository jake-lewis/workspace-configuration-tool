package model.commands;

import java.io.File;

public class OpenConfigCommand implements Command {

    private File file;

    public OpenConfigCommand(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    @Override
    public String getName() {
        return "Open Configuration File: " + file.getName();
    }
}
