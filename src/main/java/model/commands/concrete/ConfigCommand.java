package model.commands.concrete;

import model.commands.UndoableCommand;
import model.configuration.Configuration;
import model.configuration.ConfigurationFactory;

public abstract class ConfigCommand implements UndoableCommand {

    private Configuration prevConfig;
    private Configuration newConfig;

    public ConfigCommand(Configuration newConfiguration) {
        this(newConfiguration, ConfigurationFactory.getNullConfig());
    }

    public ConfigCommand(Configuration newConfiguration, Configuration previousConfiguration) {
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
    public abstract String getName();
}
