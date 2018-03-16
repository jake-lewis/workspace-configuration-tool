package model.commands.concrete;

import model.strategies.ApplyToRootStrategy;

public class ApplyToRootCommand extends ApplyConfigCommand {

    public ApplyToRootCommand(String sourcePath, String targetPath) {
        super(new ApplyToRootStrategy(sourcePath, targetPath));
    }

    @Override
    public String getName() {
        return "Apply configuration to " + this.getSourceFolder().getName();
    }
}