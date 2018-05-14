package model.configuration;

import model.ExceptionAlert;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigurationFactory {

    public static Configuration getNullConfig() {
        return NullConfiguration.getInstance();
    }

    public static Configuration create(File file) throws IOException, InvalidConfigurationException {
        if (null == file) {
            return NullConfiguration.getInstance();
        }

        FileType type = FileType.fromFile(file);

        switch (type) {
            case XML:
                return new XMLConfiguration(file);
            default:
                throw new IOException("Unsupported File Type");
        }
    }

    public static Configuration create(File directory, FileType TYPE) throws InvalidConfigurationException, IOException {
        switch (TYPE) {
            case XML:
                return new XMLConfiguration(directory);
            default:
                throw new TypeNotPresentException(TYPE.name(), new IllegalArgumentException());
        }
    }

    public static Configuration create(String text, FileType TYPE) throws IOException, InvalidConfigurationException {
        switch (TYPE) {
            case XML:
                try {
                    return new XMLConfiguration(text);
                } catch (ParseException | ParserConfigurationException | SAXException | TransformerException | IOException e) {
                    throw new InvalidConfigurationException(e.getMessage(), e);
                }
            default:
                throw new TypeNotPresentException(TYPE.name(), new IllegalArgumentException());
        }
    }

    public static Configuration create(FileType TYPE) throws InvalidConfigurationException {

        switch (TYPE) {
            case XML:
                try {
                    return new XMLConfiguration("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                            "<structure xmlns=\"https://github.com/jsjlewis96/project\"\n" +
                            "           xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                            "           xsi:schemaLocation=\"https://github.com/jsjlewis96/project workspaceConfigSchema.xsd\">\n" +
                            "    <config>\n" +
                            "        <project>New Project</project>\n" +
                            "        <root></root>\n" +
                            "        <target></target>\n" +
                            "    </config>\n" +
                            "    <dirsRoot>\n" +
                            "    </dirsRoot>\n" +
                            "</structure>");
                } catch (ParseException | ParserConfigurationException | SAXException | TransformerException | IOException e) {
                    e.printStackTrace();
                }
            default:
                throw new TypeNotPresentException(TYPE.name(), new IllegalArgumentException());
        }
    }

    public static List<Directory> directoriesFromFolder(File parentFolder) throws InvalidConfigurationException {
        return directoriesFromFolder(parentFolder, null, false);
    }

    public static List<Directory> directoriesFromFolder(File parentFolder, boolean includeFiles) throws InvalidConfigurationException {
        return directoriesFromFolder(parentFolder, null, includeFiles);
    }

    private static List<Directory> directoriesFromFolder(File parentFolder, Directory parent, boolean includeFiles) throws InvalidConfigurationException {
        List<Directory> directories = new LinkedList<>();
        List<File> dirs = Arrays.asList(Objects.requireNonNull(parentFolder.listFiles()));
        dirs = new LinkedList<>(dirs);

        Pattern prefixPattern = Pattern.compile("(\\w+) (.*)");
        Pattern namePattern = Pattern.compile("v");
        Pattern separatorPattern = Pattern.compile("([^\\w\\d\\s]|[_+*?^$.])");
        Pattern enumerationPattern = Pattern.compile("([^\\w\\d\\s]|[_+*?^$.])(?:\\w+)(?:-\\d{1,5})? ");

        for (File file : dirs) {

            if (file.isDirectory()) {
                Directory dir = new Directory(parent);
                //set dir properties
                Matcher prefixMatcher = prefixPattern.matcher(file.getName());
                Matcher nameMatcher = namePattern.matcher(file.getName());

                //if has prefix
                if (prefixMatcher.find()) {
                    dir.setPrefix(prefixMatcher.group(1));
                    dir.setName(prefixMatcher.group(2));

                    if (parent != null) {
                        //Get separator, only uses first child for simplicity
                        List<File> children = Arrays.asList(Objects.requireNonNull(parentFolder.listFiles()));
                        for (File child : children) {
                            if (child.isDirectory()) {
                                Matcher separatorMatcher = separatorPattern.matcher(child.getName());
                                while (separatorMatcher.find()) { //use last found instance of a separator, avoids setting from parent
                                    parent.setSeparator(separatorMatcher.group(1));
                                }
                                break;
                            } else {
                                Matcher separatorMatcher = enumerationPattern.matcher(child.getName());
                                while (separatorMatcher.find()) { //use last found instance of a separator, avoids setting from parent
                                    parent.setSeparator(separatorMatcher.group(1));
                                }
                                break;
                            }
                        }
                    }
                } else if (nameMatcher.find()) { //else if only has name
                    dir.setName(nameMatcher.group(1));
                } else {
                    throw new InvalidConfigurationException("Error parsing folder: " + file.getName()
                            + ". A valid directory name could not be found");
                }

                dir.setChildren(directoriesFromFolder(file, dir, includeFiles));
                directories.add(dir);
            } else if (includeFiles) {
                Directory dir = new Directory(parent, true);
                dir.setName(file.getName());
                directories.add(dir);
            }
        }

        return directories;
    }
}
