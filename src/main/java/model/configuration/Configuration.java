package model.configuration;

import java.util.List;

public interface Configuration {
    String getProjectName();

    String getProjectRootPath();

    String getProjectTargetPath();

    List<Directory> getDirectories();

    String getTextContent();

    @Override
    String toString();
}
