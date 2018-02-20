package controllers.editor;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.GridPane;
import model.configuration.Directory;
import model.configuration.Configuration;

import java.util.List;

public class VisualEditorController implements EditorController {

    private TreeView<Directory> visualEditor;
    private TextField projectNameField;
    private TextField rootField;
    private TextField targetField;
    private TextField nodeNameField;
    private TextField prefixField;
    private TextField separatorField;

    public VisualEditorController(TreeView visualEditor, GridPane projectProperties, GridPane nodeProperties) {
        this.visualEditor = visualEditor;

        ObservableList<Node> gridPaneChildren = projectProperties.getChildren();
        for (Node node : gridPaneChildren) {
            if (node.getId() != null) {
                switch (node.getId()) {
                    case "projectNameField":
                        projectNameField = (TextField) node;
                        break;
                    case "rootField":
                        rootField = (TextField) node;
                        break;
                    case "targetField":
                        targetField = (TextField) node;
                }
            }
        }

        gridPaneChildren = nodeProperties.getChildren();
        for (Node node : gridPaneChildren) {
            if (node.getId() != null) {
                switch (node.getId()) {
                    case "nodeNameField":
                        nodeNameField = (TextField) node;
                        break;
                    case "prefixField":
                        prefixField = (TextField) node;
                        break;
                    case "separatorField":
                        separatorField = (TextField) node;
                }
            }
        }
    }

    public void populate(Configuration configuration) {

        projectNameField.setText(configuration.getProjectName());
        rootField.setText(configuration.getProjectRootPath());
        targetField.setText(configuration.getProjectTargetPath());

        List<Directory> directories = configuration.getDirectories();
        TreeItem<Directory> treeRoot = new TreeItem<>();
        for (Directory rootDir : directories) {
            treeRoot.getChildren().add(createTreeItem(rootDir));
        }

        visualEditor.getSelectionModel().selectedItemProperty()
                .addListener((observable, old_val, new_val) -> {
                    TreeItem<Directory> selectedItem = new_val;
                    nodeNameField.setText(selectedItem.getValue().getName());
                    prefixField.setText(selectedItem.getValue().getDirectPrefix());
                    separatorField.setText(selectedItem.getValue().getSeparator());
                });

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
