package model.commands.concrete;

import model.configuration.Configuration;

public class OpenConfigCommand extends ConfigCommand {
    public OpenConfigCommand(Configuration newConfiguration) {
        super(newConfiguration);
    }

    public OpenConfigCommand(Configuration newConfiguration, Configuration previousConfiguration) {
        super(newConfiguration, previousConfiguration);
    }

    @Override
    public String getName() {
        return "Open configuration file: " + getNextConfig().toString();
    }
}