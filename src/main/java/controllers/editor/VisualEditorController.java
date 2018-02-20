package controllers.editor;

import controllers.CommandDelegator;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.GridPane;
import model.commands.concrete.ExpandTreeDirCommand;
import model.commands.concrete.OpenConfigCommand;
import model.commands.concrete.SelectTreeDirCommand;
import model.configuration.Directory;
import model.configuration.Configuration;
import model.executors.UndoableExecutor;

import java.util.List;

public class VisualEditorController implements EditorController {

    //DEBUG switch?
    private boolean undoableUI = true;

    private TreeView<Directory> visualEditor;
    private TextField projectNameField;
    private TextField rootField;
    private TextField targetField;
    private TextField nodeNameField;
    private TextField prefixField;
    private TextField separatorField;

    private boolean isExecuting = false;

    public VisualEditorController(TreeView<Directory> visualEditor, GridPane projectProperties, GridPane nodeProperties) {
        this.visualEditor = visualEditor;

        CommandDelegator.getINSTANCE().subscribe(new SelectionExecutor(), SelectTreeDirCommand.class);
        CommandDelegator.getINSTANCE().subscribe(new ExpansionExecutor(), ExpandTreeDirCommand.class);

        this.visualEditor.getSelectionModel().selectedItemProperty()
                .addListener((observable, old_val, new_val) -> {
                    try {
                        if (null != new_val) {
                            if (!isExecuting) {
                                int prev = visualEditor.getRow(old_val);
                                int next = visualEditor.getRow(new_val);
                                CommandDelegator.getINSTANCE().publish(
                                        new SelectTreeDirCommand(prev, next, new_val.getValue().toString()), undoableUI);
                            }
                        }
                    } catch (Exception e) { //TODO handle exception better?
                        e.printStackTrace();
                    }
                });

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

        visualEditor.setRoot(treeRoot);
        visualEditor.setShowRoot(false);
    }

    private TreeItem createTreeItem(Directory dir) {
        TreeItem<Directory> item = new TreeItem<>(dir);
        List<Directory> children = dir.getChildren();
        for (Directory child : children) {
            item.getChildren().add(createTreeItem(child));
        }

        item.expandedProperty().addListener((observable, oldValue, newValue) -> {
            if (!isExecuting) {
                try {
                    CommandDelegator.getINSTANCE().publish(
                            new ExpandTreeDirCommand(visualEditor.getRow(item), item.getValue().toString()), undoableUI);
                } catch (Exception e) { //TODO handle exception better?
                    e.printStackTrace();
                }
            }
        });
        return item;
    }

    private class SelectionExecutor implements UndoableExecutor<SelectTreeDirCommand> {

        private SelectionModel<TreeItem<Directory>> model;

        @Override
        public void unexecute(SelectTreeDirCommand command) {
            isExecuting = true;
            model = visualEditor.getSelectionModel();
            model.clearSelection();
            if (command.getPrevItem() == -1) {
                nodeNameField.setText(null);
                prefixField.setText(null);
                separatorField.setText(null);
            } else {
                model.select(command.getPrevItem());
                Directory prevSelectedItem = visualEditor.getTreeItem(command.getPrevItem()).getValue();
                nodeNameField.setText(prevSelectedItem.getName());
                prefixField.setText(prevSelectedItem.getDirectPrefix());
                separatorField.setText(prevSelectedItem.getSeparator());
            }
            isExecuting = false;
        }

        @Override
        public void reexecute(SelectTreeDirCommand command) {
            isExecuting = true;
            model = visualEditor.getSelectionModel();
            model.clearSelection();
            model.select(command.getNextItem());
            execute(command);
            isExecuting = false;
        }

        @Override
        public void execute(SelectTreeDirCommand command) {
            Directory nextSelectedItem = visualEditor.getTreeItem(command.getNextItem()).getValue();
            nodeNameField.setText(nextSelectedItem.getName());
            prefixField.setText(nextSelectedItem.getDirectPrefix());
            separatorField.setText(nextSelectedItem.getSeparator());
        }
    }

    private class ExpansionExecutor implements UndoableExecutor<ExpandTreeDirCommand> {

        @Override
        public void unexecute(ExpandTreeDirCommand command) throws Exception {
            isExecuting = true;
            TreeItem<Directory> item = visualEditor.getTreeItem(command.getIndex());
            BooleanProperty expanded = item.expandedProperty();
            expanded.set(!expanded.get());
            isExecuting = false;
        }

        @Override
        public void reexecute(ExpandTreeDirCommand command) throws Exception {
            unexecute(command);
        }

        @Override
        public void execute(ExpandTreeDirCommand command) throws Exception {
            //NOOP
        }
    }
}
