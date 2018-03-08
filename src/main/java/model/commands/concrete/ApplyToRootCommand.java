package model.commands.concrete;

import model.strategies.ApplyToRoot;

public class ApplyToRootCommand extends ApplyConfigCommand {

    public ApplyToRootCommand(String sourcePath, String targetPath) {
        super(new ApplyToRoot(sourcePath, targetPath));
    }

    @Override
    public String getName() {
        return "Apply configuration to " + this.getSourceFolder().getName();
    }
}