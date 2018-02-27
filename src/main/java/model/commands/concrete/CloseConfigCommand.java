package model.commands.concrete;

import model.commands.UndoableCommand;
import model.configuration.Configuration;

public class CloseConfigCommand implements UndoableCommand {

    private Configuration configuration;

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public String getName() {
        return "Close configuration: " + configuration.getProjectName();
    }
}
