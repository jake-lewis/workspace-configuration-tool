package model.commands.concrete;

import javafx.scene.control.TreeView;
import model.commands.UndoableCommand;

public class CollapseAllTreeCommand implements UndoableCommand {
    private TreeView prevTreeView;
    private TreeView newTreeView;
    private final String treeName;

    public CollapseAllTreeCommand(TreeView treeView, String treeName) {
        this.prevTreeView = treeView;
        this.treeName = treeName;
    }

    public TreeView getPrevTreeView() {
        return prevTreeView;
    }

    public TreeView getNewTreeView() {
        return newTreeView;
    }

    public void setNewTreeView(TreeView newTreeView) {
        this.newTreeView = newTreeView;
    }

    @Override
    public String getName() {
        return "Collapse all items in " + treeName;
    }

    public void setPrevTreeView(TreeView prevTreeView) {
        this.prevTreeView = prevTreeView;
    }
}