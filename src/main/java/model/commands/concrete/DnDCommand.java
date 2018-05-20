package model.commands.concrete;

import model.commands.UndoableCommand;
import model.configuration.Configuration;

public class DnDCommand implements UndoableCommand {

    private final Configuration newConfig;
    private final Configuration prevConfig;
    private String name;

    public DnDCommand(Configuration newConfig, Configuration prevConfig, String name) {

        this.newConfig = newConfig;
        this.prevConfig = prevConfig;
        this.name = name;
    }

    public Configuration getNewConfig() {
        return newConfig;
    }

    public Configuration getPrevConfig() {
        return prevConfig;
    }

    @Override
    public String getName() {
        return "Drag & Drop directory: " + name;
    }
}
