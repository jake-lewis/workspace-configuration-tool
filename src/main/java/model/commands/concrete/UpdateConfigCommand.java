package model.commands.concrete;

import model.configuration.Configuration;

public class UpdateConfigCommand extends ConfigCommand {
    public UpdateConfigCommand(Configuration newConfiguration) {
        super(newConfiguration);
    }

    public UpdateConfigCommand(Configuration newConfiguration, Configuration previousConfiguration) {
        super(newConfiguration, previousConfiguration);
    }

    @Override
    public String getName() {
        return "Update configuration file: " + getPrevConfig().toString();
    }
}
