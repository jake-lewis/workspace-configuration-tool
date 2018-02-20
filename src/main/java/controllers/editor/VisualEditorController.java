package controllers.editor;

import controllers.CommandDelegator;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.GridPane;
import model.commands.concrete.OpenConfigCommand;
import model.commands.concrete.SelectTreeDirCommand;
import model.configuration.Directory;
import model.configuration.Configuration;
import model.executors.UndoableExecutor;

import java.util.List;

public class VisualEditorController implements EditorController, UndoableExecutor<SelectTreeDirCommand> {

    //DEBUG switch?
    private boolean undoableUI = true;

    private TreeView<Directory> visualEditor;
    private TextField projectNameField;
    private TextField rootField;
    private TextField targetField;
    private TextField nodeNameField;
    private TextField prefixField;
    private TextField separatorField;

    public VisualEditorController(TreeView visualEditor, GridPane projectProperties, GridPane nodeProperties) {
        this.visualEditor = visualEditor;

        CommandDelegator.getINSTANCE().subscribe(this, SelectTreeDirCommand.class);

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
                    try {
                        if (null != new_val) {
                            CommandDelegator.getINSTANCE().publish(new SelectTreeDirCommand(old_val, new_val), undoableUI);
                        }
                    } catch (Exception e) { //TODO handle exception better?
                        e.printStackTrace();
                    }
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

    @Override
    public void unexecute(SelectTreeDirCommand command) throws Exception {
        if (null == command.getPrevItem()) {
            nodeNameField.setText(null);
            prefixField.setText(null);
            separatorField.setText(null);
        } else {
            Directory prevSelectedItem = command.getPrevItem().getValue();
            nodeNameField.setText(prevSelectedItem.getName());
            prefixField.setText(prevSelectedItem.getDirectPrefix());
            separatorField.setText(prevSelectedItem.getSeparator());
        }
    }

    @Override
    public void execute(SelectTreeDirCommand command) throws Exception {
        Directory nextSelectedItem = command.getNextItem().getValue();
        nodeNameField.setText(nextSelectedItem.getName());
        prefixField.setText(nextSelectedItem.getDirectPrefix());
        separatorField.setText(nextSelectedItem.getSeparator());
    }
}
