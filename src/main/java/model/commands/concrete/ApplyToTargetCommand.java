package model.commands.concrete;

import model.strategies.ApplyToTarget;

public class ApplyToTargetCommand extends ApplyConfigCommand {

    public ApplyToTargetCommand(String sourcePath, String targetPath) {
        super(new ApplyToTarget(sourcePath, targetPath));
    }

    @Override
    public String getName() {
        return "Apply configuration to " + this.getTargetFolder().getName();
    }
}