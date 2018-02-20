package model.commands.concrete;

import model.commands.UndoableCommand;

public class ExpandTreeDirCommand implements UndoableCommand {

    private int index;
    private String selectionName;

    public ExpandTreeDirCommand(int index, String selectionName) {
        this.index = index;
        this.selectionName = selectionName;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String getName() {
        return "Toggle expansion of folder: " + selectionName;
    }
}
