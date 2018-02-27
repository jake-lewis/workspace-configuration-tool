package model.configuration;

import java.io.File;
import java.util.Arrays;
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
}