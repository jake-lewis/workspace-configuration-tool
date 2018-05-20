package model.commands.concrete;

import javafx.scene.control.TreeView;
import model.commands.UndoableCommand;

public class ToggleExpandTreeItemCommand implements UndoableCommand {

    private final TreeView treeView;
    private int index;
    private String selectionName;

    public ToggleExpandTreeItemCommand(TreeView treeView, int index, String selectionName) {
        this.treeView = treeView;
        this.index = index;
        this.selectionName = selectionName;
    }

    public TreeView getTreeView() {
        return treeView;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String getName() {
        return "Toggle expansion of folder: " + selectionName;
    }
}