package model.commands.concrete;

import javafx.scene.control.TreeView;
import model.strategies.treeexpansion.ExpandTreeViewStrategy;

public class ExpandAllTreeCommand extends ToggleTreeExpansionCommand {

    public ExpandAllTreeCommand(TreeView treeView, String treeName) {
        super(new ExpandTreeViewStrategy(treeView, treeName));
    }

    @Override
    public String getName() {
        return "Expand all items in " + getStrategy().getTreeName();
    }
}