package model.strategies;

import model.configuration.ConfigurationFactory;
import model.configuration.Directory;
import model.configuration.InvalidConfigurationException;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.io.FileUtils.copyFileToDirectory;

public class ApplyToTarget extends ConfigApplicator {
    public ApplyToTarget(String rootPath, String targetPath) {
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
                if (child.isDirectory()) {
                    //use list iterator to allow removal on the fly
                    ListIterator<Directory> listIterator = (new LinkedList<>(ConfigurationFactory
                            .directoriesFromFolder(this.getRootFolder(), true))).listIterator();
                    moveToTarget(this.getRootFolder(), child, listIterator);

                    listIterator.forEachRemaining(remaining::add);
                }
            }
        }

        return remaining;
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