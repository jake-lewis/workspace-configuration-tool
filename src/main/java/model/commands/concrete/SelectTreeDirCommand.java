package model.commands.concrete;

import model.commands.UndoableCommand;

public class SelectTreeDirCommand implements UndoableCommand {

    private int prevItem;
    private int nextItem;
    private String selectionName;

    public SelectTreeDirCommand(int prevItem, int nextItem, String selectionName) {
        this.prevItem = prevItem;
        this.nextItem = nextItem;
        this.selectionName = selectionName;
    }

    public int getPrevItem() {
        return prevItem;
    }

    public int getNextItem() {
        return nextItem;
    }

    @Override
    public String getName() {
        return "Select folder: " + selectionName;
    }
}
