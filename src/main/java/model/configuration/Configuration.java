package model.configuration;

import java.text.ParseException;
import java.util.List;

public interface Configuration {

    void setTextContent(String textContent) throws ParseException, InvalidConfigurationException;

    String getProjectName();

    String getProjectRootPath();

    String getProjectTargetPath();

    List<Directory> getDirectories();

    String getTextContent();

    @Override
    String toString();
}
