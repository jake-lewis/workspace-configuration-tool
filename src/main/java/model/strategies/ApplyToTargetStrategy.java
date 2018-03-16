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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.io.FileUtils.copyFileToDirectory;

public class ApplyToTargetStrategy extends ConfigApplicator {

    private boolean overwrite = false;
    private boolean overwriteSet = false;
    private boolean isCancel = false;

    public ApplyToTargetStrategy(String rootPath, String targetPath) {
        super(rootPath, targetPath);
    }

    @Override
    public List<Directory> apply() throws InvalidConfigurationException {
        List<Directory> remaining = new LinkedList<>();

        //If there are valid folders
        if (this.getRootFolder() != null && this.getTargetFolder() != null) {
            List<File> children = new LinkedList<>(Arrays.asList(Objects.requireNonNull(
                    this.getTargetFolder().listFiles(File::isDirectory))));
            //Ignores top level folder, assumes structure is correct
            for (File child : children) {
                if (child.isDirectory() && !isCancel) {
                    //use list iterator to allow removal on the fly
                    ListIterator<Directory> listIterator = (new LinkedList<>(ConfigurationFactory
                            .directoriesFromFolder(this.getRootFolder(), true))).listIterator();
                    moveToTarget(child, listIterator);

                    listIterator.forEachRemaining(remaining::add);
                }
            }
        }

        overwrite = false;
        overwriteSet = false;
        isCancel = false;
        return remaining;
    }

    private void moveToTarget(File targetFolder, ListIterator<Directory> directoryList) {
        List<File> children = new LinkedList<>(Arrays.asList(Objects.requireNonNull(targetFolder.listFiles(File::isDirectory))));

        boolean overwriteFolder = false;
        boolean overwriteFolderSet = false;
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
                        File sourceFile = new File(this.getRootFolder().getPath() + "\\" + current.getName());
                        boolean fileExists = new File(targetFolder.getPath() + "\\" + current.getName()).isFile();
                        boolean copy = false;

                        if (!fileExists || overwrite || overwriteFolder) {
                            copy = true;
                        } else if (!overwriteSet && !overwriteFolderSet) {
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Overwrite Dialog");
                            alert.setHeaderText("File already exists");
                            alert.setContentText("Would you like to overwrite '" + targetFolder.getPath() + "\\" + current.getName() + "'?");

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
                                copyFileToDirectory(sourceFile, targetFolder);
                                directoryList.remove();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
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
            if (directoryList.hasNext() && !isCancel) {
                moveToTarget(child, directoryList);
            }
        }
    }
}