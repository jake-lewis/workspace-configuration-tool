package model.strategies;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import model.configuration.ConfigurationFactory;
import model.configuration.Directory;
import model.configuration.InvalidConfigurationException;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.apache.commons.io.FileUtils.copyFileToDirectory;

public class ApplyToRootStrategy extends ConfigApplicator {

    private boolean overwrite, overwriteSet, isCancel = false;
    private List<Directory> remaining;

    public ApplyToRootStrategy(String sourcePath, String targetPath) {
        super(sourcePath, targetPath);
    }

    @Override
    public List<Directory> apply() throws InvalidConfigurationException {
        remaining = new LinkedList<>();

        //If there are valid folders
        if (this.getRootFolder() != null && this.getTargetFolder() != null) {
            File source = this.getTargetFolder();

            ListIterator<Directory> listIterator = (new LinkedList<>(ConfigurationFactory
                    .directoriesFromFolder(source, true))).listIterator();

            moveToRoot(source, listIterator);
        }

        overwrite = false;
        overwriteSet = false;
        isCancel = false;
        return remaining;
    }

    private void moveToRoot(File sourceFolder, ListIterator<Directory> directoryList) {

        boolean overwriteFolder = false;
        boolean overwriteFolderSet = false;
        List<File> children = new LinkedList<>(Arrays.asList(Objects.requireNonNull(
                sourceFolder.listFiles())));

        for (File child : children) {
            Directory current = directoryList.next();
            if (child.isDirectory() && !isCancel) {
                moveToRoot(child, current.getChildren().listIterator());
            } else {
                boolean fileExists = new File(this.getRootFolder().getPath() + "\\" + child.getName()).isFile();
                boolean copy = false;

                if (!fileExists || overwrite || overwriteFolder) {
                    copy = true;
                } else if (!overwriteSet && !overwriteFolderSet) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Overwrite Dialog");
                    alert.setHeaderText("File already exists");
                    alert.setContentText("Would you like to overwrite '" + child.getName() +
                            "' with the version from '" + sourceFolder.getPath() +"'?");

                    ButtonType yes = new ButtonType("Yes");
                    ButtonType no = new ButtonType("No");
                    ButtonType yesFolder = new ButtonType("Yes to folder");
                    ButtonType noFolder = new ButtonType("No to folder");
                    ButtonType yesAll = new ButtonType("Yes to all");
                    ButtonType noAll = new ButtonType("No to all");
                    ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

                    alert.getButtonTypes().setAll(yes, no, yesFolder, noFolder, yesAll, noAll, cancel);

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent()) {
                        ButtonType choice = result.get();

                        if (choice == yes) {
                            copy = true;
                        } else if (choice == no) {
                            copy = false;
                        } else if (choice == yesFolder || choice == noFolder) {
                            overwriteFolderSet = true;
                            overwriteFolder = (choice == yesFolder);
                            copy = overwriteFolder;
                        } else if (choice == yesAll || choice == noAll) {
                            overwriteSet = true;
                            overwrite = (choice == yesAll);
                            copy = overwrite;
                        } else if (choice == cancel) {
                            isCancel = true;
                            break;
                        }
                    }
                }

                if (copy) {
                    try {
                        copyFileToDirectory(child, this.getRootFolder());
                    } catch (IOException e) {
                        remaining.add(current);
                        e.printStackTrace();
                    }
                } else {
                    remaining.add(current);
                }
            }
        }
    }
}
