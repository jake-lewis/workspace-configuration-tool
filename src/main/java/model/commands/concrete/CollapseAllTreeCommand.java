package model.commands.concrete;

import javafx.scene.control.TreeView;
import model.strategies.CollapseTreeViewStrategy;

public class CollapseAllTreeCommand extends ToggleTreeExpansionCommand {

    public CollapseAllTreeCommand(TreeView treeView, String treeName) {
        super(new CollapseTreeViewStrategy(treeView, treeName));
    }

    @Override
    public String getName() {
        return "Collapse all items in " + getStrategy().getTreeName();
    }
}