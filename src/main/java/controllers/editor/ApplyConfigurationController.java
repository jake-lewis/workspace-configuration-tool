package controllers.editor;

import controllers.CommandDelegator;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import model.ExceptionAlert;
import model.commands.concrete.ApplyConfigCommand;
import model.commands.concrete.ApplyToRootCommand;
import model.commands.concrete.ApplyToTargetCommand;
import model.configuration.Configuration;
import model.configuration.ConfigurationFactory;
import model.configuration.Directory;
import model.configuration.InvalidConfigurationException;
import model.executors.Executor;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;

public class ApplyConfigurationController implements EditorController {

    //DEBUG switch?
    private boolean undoableUI = true;

    private Configuration configuration;
    private List<Directory> rootDirectories;
    private List<Directory> targetDirectories;
    private TextField projectNameField;
    private TextField rootField;
    private TextField targetField;
    private TreeView<Directory> rootVisualEditor;
    private TreeView<Directory> targetVisualEditor;

    private boolean isExecuting = false;

    public ApplyConfigurationController(Tab applyConfigTab) {

        CommandDelegator.getINSTANCE().subscribe(new ApplyConfigExecutor(), ApplyConfigCommand.class);
        BorderPane applyConfigPane = (BorderPane) applyConfigTab.getContent();
        GridPane configurationProperties = (GridPane) ((BorderPane) applyConfigPane.getLeft()).getTop();
        SplitPane splitPane = (SplitPane) applyConfigPane.getCenter();
        ObservableList<Node> splitPaneChildren = splitPane.getItems();
        for (Node node : splitPaneChildren) {
            if (node.getId() != null) {
                switch (node.getId()) {
                    case "rootPane":
                        this.rootVisualEditor = (TreeView<Directory>) ((BorderPane) node).getCenter();
                    case "targetPane":
                        this.targetVisualEditor = (TreeView<Directory>) ((BorderPane) node).getCenter();
                }
            }
        }

        ObservableList<Node> gridPaneChildren = configurationProperties.getChildren();
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
                    case "rootToTargetBtn":
                        ((Button) node).setOnAction(event -> {
                            if (!rootField.getText().isEmpty() && !targetField.getText().isEmpty()) {
                                try {
                                    CommandDelegator.getINSTANCE().publish(
                                            new ApplyToTargetCommand(rootField.getText(), targetField.getText()));
                                } catch (Exception e) {
                                    ExceptionAlert alert = new ExceptionAlert(e);
                                    alert.showAndWait();
                                }
                            } else {
                                ExceptionAlert alert = new ExceptionAlert(
                                        new InvalidConfigurationException("A configuration with a valid root and " +
                                                "target path must be loaded before you can configure a workspace"));
                                alert.showAndWait();
                            }
                        });
                        break;
                    case "targetToRootBtn":
                        ((Button) node).setOnAction(event -> {
                            if (!rootField.getText().isEmpty() && !targetField.getText().isEmpty()) {
                                try {
                                    CommandDelegator.getINSTANCE().publish(
                                            new ApplyToRootCommand(rootField.getText(), targetField.getText()));
                                } catch (Exception e) {
                                    ExceptionAlert alert = new ExceptionAlert(e);
                                    alert.showAndWait();
                                }
                            } else {
                                ExceptionAlert alert = new ExceptionAlert(
                                        new InvalidConfigurationException("A configuration with a valid root and " +
                                                "target path must be loaded before you can configure a workspace"));
                                alert.showAndWait();
                            }
                        });
                }
            }
        }
    }

    @Override
    public void populate(Configuration configuration) {
        this.configuration = configuration;

        //The value itself should not be null,
        // but this will return true if it is an instance of NullConfiguration
        if (!this.configuration.equals(null)) {

            projectNameField.setText(configuration.getProjectName());
            rootField.setText(configuration.getProjectRootPath());
            targetField.setText(configuration.getProjectTargetPath());

            try {
                if (configuration.getProjectRootPath() != null) {
                    File file = new File(configuration.getProjectRootPath());
                    if (file.isDirectory()) {
                        rootDirectories = populateEditor(file, rootVisualEditor);
                    }
                }
                if (configuration.getProjectTargetPath() != null) {
                    File file = new File(configuration.getProjectTargetPath());
                    if (file.isDirectory()) {
                        targetDirectories = populateEditor(file, targetVisualEditor);
                    }
                }
            } catch (InvalidConfigurationException e) {
                ExceptionAlert alert = new ExceptionAlert(e);
                alert.showAndWait();
            }
        } else {
            projectNameField.setText(null);
            rootField.setText(null);
            targetField.setText(null);
            rootVisualEditor.setRoot(null);
            targetVisualEditor.setRoot(null);
        }
    }

    private List<Directory> populateEditor(File file, TreeView<Directory> treeView) throws
            InvalidConfigurationException {
        List<Directory> directories = ConfigurationFactory.directoriesFromFolder(
                file, true);

        TreeItem<Directory> treeRoot = new TreeItem<>();
        if (directories != null) {
            for (Directory rootDir : directories) {
                treeRoot.getChildren().add(createTreeItem(rootDir));
            }
        }

        treeView.setRoot(treeRoot);
        treeView.setShowRoot(false);
        return directories;
    }

    private TreeItem createTreeItem(Directory dir) {
        TreeItem<Directory> item = new TreeItem<>(dir);
        List<Directory> children = dir.getChildren();
        for (Directory child : children) {
            item.getChildren().add(createTreeItem(child));
        }
        return item;
    }

    private class ApplyConfigExecutor implements Executor<ApplyConfigCommand> {

        @Override
        public void execute(ApplyConfigCommand command) throws Exception {
            List<Directory> remaining = command.getApplicator().apply();
            //remaining.forEach(directory -> System.out.println(directory.toString()));
        }
    }
}
