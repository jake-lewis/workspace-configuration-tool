package controllers.editor;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import model.Configuration;
import model.Directory;

import java.util.LinkedList;
import java.util.List;

public class VisualEditorController {

    private TreeView<Directory> visualEditor;

    public VisualEditorController(TreeView visualEditor) {
        this.visualEditor = visualEditor;
    }

    public void populateVisualEditor(Configuration configuration) {
        List<Directory> directories = configuration.getDirectories();
        TreeItem<Directory> treeRoot = new TreeItem<>();
        for (Directory rootDir : directories) {
            treeRoot.getChildren().add(createTreeItem(rootDir));
        }

        visualEditor.setRoot(treeRoot);
        visualEditor.setShowRoot(false);
    }

    private TreeItem createTreeItem(Directory dir) {
        TreeItem<Directory> item = new TreeItem<>(dir);
        List<Directory> children = dir.getChildren();
        for (Directory child: children) {
            item.getChildren().add(createTreeItem(child));
        }
        return item;
    }
}
