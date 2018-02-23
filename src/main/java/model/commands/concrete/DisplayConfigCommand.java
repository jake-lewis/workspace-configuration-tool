package model.commands.concrete;

import model.commands.UndoableCommand;
import model.configuration.Configuration;
import model.configuration.ConfigurationFactory;

public class DisplayConfigCommand implements UndoableCommand {

    private Configuration prevConfig;
    private Configuration newConfig;

    public DisplayConfigCommand(Configuration newConfiguration) {
        this(newConfiguration, ConfigurationFactory.getNullConfig());
    }

    public DisplayConfigCommand(Configuration newConfiguration, Configuration previousConfiguration) {
        this.newConfig = newConfiguration;
        this.prevConfig = previousConfiguration;
    }

    public Configuration getPrevConfig() { return this.prevConfig; }

    public void setPrevConfig(Configuration prevConfig) {
        this.prevConfig = prevConfig;
    }

    public Configuration getNextConfig() {
        return newConfig;
    }

    @Override
    public String getName() {
        return "Open configuration file: " + newConfig.toString();
    }
}
