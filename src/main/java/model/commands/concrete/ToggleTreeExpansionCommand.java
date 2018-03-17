package model.commands.concrete;

import model.commands.UndoableCommand;
import model.strategies.treeexpansion.TreeViewExpansionStrategy;

public abstract class ToggleTreeExpansionCommand implements UndoableCommand {
    private TreeViewExpansionStrategy strategy;

    ToggleTreeExpansionCommand(TreeViewExpansionStrategy strategy) {
        this.strategy = strategy;
    }

    public TreeViewExpansionStrategy getStrategy() {
        return strategy;
    }
}
