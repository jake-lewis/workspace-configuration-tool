package controllers.editor;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import model.ExceptionAlert;
import model.configuration.Configuration;
import model.configuration.ConfigurationFactory;
import model.configuration.Directory;
import model.configuration.InvalidConfigurationException;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                        ((Button) node).setOnAction(event -> rootToTarget());
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

//        item.expandedProperty().addListener((observable, oldValue, newValue) -> {
//            if (!isExecuting) {
//                try {
//                    CommandDelegator.getINSTANCE().publish(
//                            new ExpandTreeDirCommand(visualEditor.getRow(item), item.getValue().toString()), undoableUI);
//                } catch (Exception e) { //TODO handle exception better?
//                    e.printStackTrace();
//                }
//            }
//        });
        return item;
    }

    private void rootToTarget() {
        if (!this.configuration.equals(null)) {
            if (this.configuration.getProjectRootPath() != null) {
                //TODO uses currently populated list of directories, maybe should actually parse folder?
                File targetDirectory = new File(this.configuration.getProjectTargetPath());
                List<File> children = new LinkedList<>(Arrays.asList(Objects.requireNonNull(targetDirectory.listFiles(File::isDirectory))));
                //Ignores top[ level folder, assumes structure is correct
                for (File child : children) {
                    if (child.isDirectory()) {
                        //use list iterator to allow removal on the fly
                        moveToTarget(child, (new LinkedList<>(rootDirectories)).listIterator());
                    }
                }
            }
        }
    }

    private void moveToTarget(File targetFolder, ListIterator<Directory> directoryList) {
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
                        System.out.println(targetFolder.getPath() + "\\" + current.getName());
                        directoryList.remove();
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
                moveToTarget(child, directoryList);
            }
        }
    }
}
