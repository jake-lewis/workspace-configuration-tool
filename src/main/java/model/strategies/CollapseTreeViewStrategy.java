package model.strategies;

import javafx.scene.control.TreeView;

public class CollapseTreeViewStrategy extends TreeViewExpansionStrategy {
    public CollapseTreeViewStrategy(TreeView treeView, String treeName) {
        super(treeView, treeName);
    }

    @Override
    public void apply() {
        execute(false);
    }
}
