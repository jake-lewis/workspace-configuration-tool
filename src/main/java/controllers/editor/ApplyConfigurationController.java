package controllers.editor;

import controllers.CommandDelegator;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import model.ExceptionAlert;
import model.commands.concrete.ToTargetCommand;
import model.configuration.Configuration;
import model.configuration.ConfigurationFactory;
import model.configuration.Directory;
import model.configuration.InvalidConfigurationException;
import model.executors.Executor;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.io.FileUtils.copyFileToDirectory;

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

        CommandDelegator.getINSTANCE().subscribe(new ToTargetExecutor(), ToTargetCommand.class);
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
                                            new ToTargetCommand(rootField.getText(), targetField.getText()));
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

    private List<Directory> populateEditor(File file, TreeView<Directory> treeView) throws InvalidConfigurationException {
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

    private class ToTargetExecutor implements Executor<ToTargetCommand> {

        @Override
        public void execute(ToTargetCommand command) throws Exception {
            //If there are valid folders
            if (command.getSourceFolder() != null && command.getTargetFolder() != null) {
                List<File> children = new LinkedList<>(Arrays.asList(Objects.requireNonNull(
                        command.getTargetFolder().listFiles(File::isDirectory))));
                //Ignores top level folder, assumes structure is correct
                for (File child : children) {
                    if (child.isDirectory()) {
                        //use list iterator to allow removal on the fly
                        ListIterator<Directory> listIterator = (new LinkedList<>(ConfigurationFactory
                                .directoriesFromFolder(command.getSourceFolder(), true))).listIterator();
                        moveToTarget(command.getSourceFolder(), child, listIterator);

                        //TODO in theory anything left in list iterator could not be moved
                    }
                }
            }
        }

        private void moveToTarget(File sourceFolder, File targetFolder, ListIterator<Directory> directoryList) {
            List<File> children = new LinkedList<>(Arrays.asList(Objects.requireNonNull(targetFolder.listFiles(File::isDirectory))));

            String fullName = targetFolder.getName();
            Pattern prefixPattern = Pattern.compile("(\\w+) (.*)");
            //Pattern for a file that may be enumerated, e.g. TQ.1.XX File.txt (the XX is sequential numbering)
            Pattern enumPrefixPattern = Pattern.compile("(.+?)(?:-\\d{1,5})? (.*)");
            Matcher prefixMatcher = prefixPattern.matcher(fullName);

            //If folder has valid prefix pattern
            if (prefixMatcher.find()) {
                String folderName = prefixMatcher.group(2);
                int nameStart = fullName.lastIndexOf(folderName);
                String folderFullPrefix = fullName.substring(0, nameStart - 1);

                //For each file, check if it is meant to be in this folder
                while (directoryList.hasNext()) {
                    Directory current = directoryList.next();
                    Matcher enumPrefixMatcher = enumPrefixPattern.matcher(current.getName());

                    if (enumPrefixMatcher.find()) {
                        String dirFullPrefix = enumPrefixMatcher.group(1);
                        //Not sure how this could happen, but I'm sure there's a good reason for it
                        if (dirFullPrefix.isEmpty()) {
                            break;
                        }

                        //If exact prefix (not including possible enumeration) matches
                        if (enumPrefixMatcher.group(1).equals(folderFullPrefix)) {
                            File sourceFile = new File(sourceFolder.getPath() + "\\" + current.getName());
                            try {
                                copyFileToDirectory(sourceFile, targetFolder);
                                directoryList.remove();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                //Reset iterator
                while (directoryList.hasPrevious()) {
                    directoryList.previous();
                }
            }

            //Recurse through sub-folders
            for (File child : children) {
                if (directoryList.hasNext()) {
                    moveToTarget(sourceFolder, child, directoryList);
                }
            }
        }
    }
}
