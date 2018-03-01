package controllers.editor;

import controllers.CommandDelegator;
import javafx.beans.property.BooleanProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import model.ExceptionAlert;
import model.commands.concrete.ExpandTreeDirCommand;
import model.commands.concrete.SelectTreeDirCommand;
import model.commands.concrete.UpdateConfigCommand;
import model.configuration.Configuration;
import model.configuration.Directory;
import model.configuration.InvalidConfigurationException;
import model.configuration.XMLConfiguration;
import model.executors.UndoableExecutor;

import java.util.LinkedList;
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
    private Configuration configuration;

    public VisualEditorController(Tab visualEditorTab) {

        BorderPane visualPane = (BorderPane) visualEditorTab.getContent();

        this.visualEditor = (TreeView<Directory>) visualPane.getCenter();
        GridPane projectProperties = (GridPane) ((BorderPane) visualPane.getLeft()).getTop();
        GridPane nodeProperties = (GridPane) ((BorderPane) visualPane.getRight()).getTop();

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
                        break;
                    case "applyConfigBtn" :
                        ((Button) node).setOnAction(event -> {
                            try {
                                applyConfigChange();
                            } catch (Exception e) {
                                new ExceptionAlert(e).showAndWait();
                            }
                        });
                        break;
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
                        break;
                    case "applyDirBtn" :
                        ((Button) node).setOnAction(event -> {
                            try {
                                applyDirectoryChange();
                            } catch (Exception e) {
                                new ExceptionAlert(e).showAndWait();
                            }
                        });
                        break;
                }
            }
        }
    }

    public void populate(Configuration configuration) {

        this.configuration = configuration;

        if (configuration != null) {
            projectNameField.setText(configuration.getProjectName());
            rootField.setText(configuration.getProjectRootPath());
            targetField.setText(configuration.getProjectTargetPath());

            List<Directory> directories = configuration.getDirectories();
            TreeItem<Directory> treeRoot = new TreeItem<>();
            if (directories != null) {
                for (Directory rootDir : directories) {
                    treeRoot.getChildren().add(createTreeItem(rootDir));
                }
            }

            visualEditor.setRoot(treeRoot);
            visualEditor.setShowRoot(false);
        } else {
            projectNameField.setText(null);
            rootField.setText(null);
            targetField.setText(null);
            this.visualEditor.setRoot(null);
        }
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

    private void applyConfigChange() throws InvalidConfigurationException, Exception {
        //TODO add support for other types of configuration
        XMLConfiguration newConfig = XMLConfiguration.copy((XMLConfiguration) configuration);
        newConfig.setProjectName(projectNameField.getText());
        newConfig.setProjectRootPath(rootField.getText());
        newConfig.setProjectTargetPath(targetField.getText());
        newConfig.updateTextContent();
        apply(newConfig);
    }

    private void applyDirectoryChange() throws InvalidConfigurationException, Exception {
        //TODO add support for other types of configuration
        XMLConfiguration newConfig = XMLConfiguration.copy((XMLConfiguration) configuration);
        TreeItem<Directory> selectedItem = visualEditor.getSelectionModel().getSelectedItem();
        List<Directory> directories = new LinkedList<>();

        for (TreeItem<Directory> item : visualEditor.getRoot().getChildren()) {
            recurseDirectories(item, selectedItem);
            directories.add(item.getValue());
        }

        newConfig.setDirectories(directories);
        apply(newConfig);
    }

    private boolean recurseDirectories(TreeItem<Directory> item, TreeItem<Directory> selectedItem) throws InvalidConfigurationException {
        if (item.equals(selectedItem)) {
            item.getValue().setPrefix(prefixField.getText());
            item.getValue().setSeparator(separatorField.getText());
            item.getValue().setName(nodeNameField.getText());
            return true;
        } else {
            for (TreeItem<Directory> subItem : item.getChildren()) {
                if (recurseDirectories(subItem, selectedItem)) {
                    return true;
                }
            }
            return false;
        }
    }

    private void apply(Configuration newConfiguration) throws Exception {
        CommandDelegator.getINSTANCE().publish(new UpdateConfigCommand(newConfiguration, configuration));
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
