package model.strategies;

import javafx.scene.control.TreeView;

public class ExpandTreeViewStrategy extends TreeViewExpansionStrategy {
    public ExpandTreeViewStrategy(TreeView treeView, String treeName) {
        super(treeView, treeName);
    }

    @Override
    public void apply() {
        execute(true);
    }
}
