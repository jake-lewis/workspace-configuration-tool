package controllers.editor;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import model.Directory;
import model.configuration.Configuration;

import java.util.List;

public class VisualEditorController implements EditorController {

    private TreeView<Directory> visualEditor;

    public VisualEditorController(TreeView visualEditor) {
        this.visualEditor = visualEditor;
    }

    public void populate(Configuration configuration) {
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
