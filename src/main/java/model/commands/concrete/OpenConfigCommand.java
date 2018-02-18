package model.commands.concrete;

import model.commands.UndoableCommand;
import model.configuration.Configuration;

import java.io.File;

public class OpenConfigCommand implements UndoableCommand {

    private File file;
    private Configuration prevConfig;
    private Configuration newConfig;

    public OpenConfigCommand(File file) {
        this(file, null);
    }

    public OpenConfigCommand(File file, Configuration previousConfiguration) {
        this.file = file;
        this.prevConfig = previousConfiguration;
    }

    public File getFile() {
        return file;
    }

    public Configuration getPrevConfig() { return this.prevConfig; }

    public void setPrevConfig(Configuration prevConfig) {
        this.prevConfig = prevConfig;
    }

    public Configuration getNextConfig() {
        return newConfig;
    }

    public void setNewConfig(Configuration newConfig) {
        this.newConfig = newConfig;
    }

    @Override
    public String getName() {
        return "Open configuration file: " + file.getName();
    }
}
