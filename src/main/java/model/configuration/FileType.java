package model.configuration;

import javafx.stage.FileChooser;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public enum FileType {
    XML("xml", "config"),
    UNSUPPORTED;

    private List<String> fileExtensions;

    FileType(String... fileExtensions) {
        this.fileExtensions = Arrays.asList(fileExtensions);
    }

    public List<String> getValues() { return this.fileExtensions; }

    public static FileType fromExtension(String extension) {
        for (FileType type : FileType.values()) {
            if (type.fileExtensions.contains(extension)) {
                return type;
            }
        }

        return UNSUPPORTED;
    }

    public static FileType fromFile(File file) {
        return fromExtension(getExtension(file));
    }

    private static String getExtension(File file) {
        String extension = "";

        int i = file.getName().lastIndexOf('.');
        if (i > 0) {
            extension = file.getName().substring(i + 1);
        }

        return extension;
    }

    public static void populateConfigurationExtensions(FileChooser fileChooser) {
        for (FileType type : FileType.values()) {
            if (type != FileType.UNSUPPORTED) {
                List<String> extensions = new LinkedList<>();
                for (String value : type.getValues()) {
                    extensions.add("*." + value);
                }
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(type.name(), extensions));
            }
        }
    }
}