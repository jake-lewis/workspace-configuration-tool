package model.commands.concrete;

import model.strategies.applyconfig.ApplyToTargetStrategy;

public class ApplyToTargetCommand extends ApplyConfigCommand {

    public ApplyToTargetCommand(String sourcePath, String targetPath) {
        super(new ApplyToTargetStrategy(sourcePath, targetPath));
    }

    @Override
    public String getName() {
        return "Apply configuration to " + this.getTargetFolder().getName();
    }
}