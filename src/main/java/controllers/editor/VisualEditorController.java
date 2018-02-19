package controllers.editor;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.GridPane;
import model.Directory;
import model.configuration.Configuration;

import java.util.List;

public class VisualEditorController implements EditorController {

    private TreeView<Directory> visualEditor;
    private TextField nameField;
    private TextField rootField;
    private TextField targetField;

    public VisualEditorController(TreeView visualEditor, GridPane propertiesPane) {
        this.visualEditor = visualEditor;

        ObservableList<Node> gridPaneChildren = propertiesPane.getChildren();
        for (Node node : gridPaneChildren) {
            if (node.getId() != null) {
                switch (node.getId()) {
                    case "nameField":
                        nameField = (TextField) node;
                        break;
                    case "rootField":
                        rootField = (TextField) node;
                        break;
                    case "targetField":
                        targetField = (TextField) node;
                }
            }
        }
    }

    public void populate(Configuration configuration) {

        nameField.setText(configuration.getProjectName());
        rootField.setText(configuration.getProjectRootPath());
        targetField.setText(configuration.getProjectTargetPath());

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
        for (Directory child : children) {
            item.getChildren().add(createTreeItem(child));
        }
        return item;
    }
}
