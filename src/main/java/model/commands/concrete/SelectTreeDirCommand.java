package model.commands.concrete;

import javafx.scene.control.TreeItem;
import model.commands.UndoableCommand;
import model.configuration.Directory;

public class SelectTreeDirCommand implements UndoableCommand {

    private TreeItem<Directory> prevItem;
    private TreeItem<Directory> nextItem;

    public SelectTreeDirCommand(TreeItem<Directory> prevItem, TreeItem<Directory> nextItem) {
        this.prevItem = prevItem;
        this.nextItem = nextItem;
    }

    public TreeItem<Directory> getPrevItem() {
        return prevItem;
    }

    public TreeItem<Directory> getNextItem() {
        return nextItem;
    }

    @Override
    public String getName() {
        return "Select folder: " + nextItem.getValue().toString();
    }
}
