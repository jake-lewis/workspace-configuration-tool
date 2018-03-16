package model.strategies;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public abstract class TreeViewExpansionStrategy {
    private TreeView editorHandle;
    private TreeItem prevTreeRoot;
    private TreeItem newTreeRoot;
    private final String treeName;

    TreeViewExpansionStrategy(TreeView treeView, String treeName) {
        this.editorHandle = treeView;
        this.treeName = treeName;
    }

    private TreeView getEditorHandle() {
        return editorHandle;
    }

    public String getTreeName() {
        return treeName;
    }

    private TreeItem getPrevTreeRoot() {
        return prevTreeRoot;
    }

    private TreeItem getNewTreeRoot() {
        return newTreeRoot;
    }

    private void setPrevTreeRoot(TreeItem prevTreeRoot) {
        this.prevTreeRoot = prevTreeRoot;
    }

    private void setNewTreeRoot(TreeItem newTreeRoot) {
        this.newTreeRoot = newTreeRoot;
    }

    void execute(boolean expanded) {
        TreeView<?> editor = getEditorHandle();
        setPrevTreeRoot(copyTreeItem(editor.getRoot()));
        setTreeItemExpansion(editor.getRoot(), expanded);
        setNewTreeRoot(copyTreeItem(editor.getRoot()));
    }

    private void setTreeItemExpansion(TreeItem<?> item, boolean expanded) {
        if (item != null && !item.isLeaf()) {

            //Don't collapse/expand root
            if (item.getParent() != null) {
                item.setExpanded(expanded);
            }

            for (TreeItem<?> child : item.getChildren()) {
                setTreeItemExpansion(child, expanded);
            }
        }
    }

    private TreeItem copyTreeItem(TreeItem item) {
        TreeItem copy = new TreeItem();
        copy.setValue(item.getValue());
        copy.setGraphic(item.getGraphic());
        copy.setExpanded(item.isExpanded());

        for (Object subItem : item.getChildren()) {
            copy.getChildren().add(copyTreeItem((TreeItem) subItem));
        }

        return copy;
    }

    public abstract void apply();

    public void unapply() {
        getEditorHandle().setRoot(getPrevTreeRoot());
    }

    public void reapply() {
        getEditorHandle().setRoot(getNewTreeRoot());
    }
}
