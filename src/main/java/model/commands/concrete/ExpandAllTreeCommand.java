package model.commands.concrete;

import javafx.scene.control.TreeView;
import model.commands.UndoableCommand;

public class ExpandAllTreeCommand implements UndoableCommand {
    private TreeView oldTreeView;
    private TreeView newTreeView;
    private final String treeName;

    public ExpandAllTreeCommand(TreeView treeView, String treeName) {
        this.oldTreeView = treeView;
        this.treeName = treeName;
    }

    public TreeView getPrevTreeView() {
        return oldTreeView;
    }

    public TreeView getNewTreeView() {
        return newTreeView;
    }

    public void setPrevTreeView(TreeView oldTreeView) {
        this.oldTreeView = oldTreeView;
    }

    public void setNewTreeView(TreeView newTreeView) {
        this.newTreeView = newTreeView;
    }

    @Override
    public String getName() {
        return "Expand all items in " + treeName;
    }
}
