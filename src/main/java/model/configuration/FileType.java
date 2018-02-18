package model.configuration;

import java.util.Arrays;
import java.util.List;

public enum FileType {
    XML("xml", "config"),
    UNSUPPORTED;

    private List<String> fileExtensions;

    FileType(String... fileExtensions) {
        this.fileExtensions = Arrays.asList(fileExtensions);
    }

    public static FileType fromExtension(String extension) {
        for (FileType type : FileType.values()) {
            if (type.fileExtensions.contains(extension)) {
                return type;
            }
        }

        return UNSUPPORTED;
    }
}