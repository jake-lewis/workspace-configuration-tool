package model.configuration;

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

        FileType type = FileType.fromExtension(getExtension(file));

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
                    e.printStackTrace();
                }
            default:
                throw new TypeNotPresentException(TYPE.name(), new IllegalArgumentException());
        }
    }

    private static String getExtension(File file) {
        String extension = "";

        int i = file.getName().lastIndexOf('.');
        if (i > 0) {
            extension = file.getName().substring(i + 1);
        }

        return extension;
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
        return directoriesFromFolder(parentFolder, null);
    }

    private static List<Directory> directoriesFromFolder(File parentFolder, Directory parent) throws InvalidConfigurationException {
        List<Directory> directories = new LinkedList<>();
        List<File> dirs = Arrays.asList(Objects.requireNonNull(parentFolder.listFiles(File::isDirectory)));
        dirs = new LinkedList<>(dirs);

        for (File folder : dirs) {
            Directory dir = new Directory(parent);

            //set dir properties
            Pattern prefixPattern = Pattern.compile("(\\w+) (.*)");
            Matcher prefixMatcher = prefixPattern.matcher(folder.getName());

            Pattern namePattern = Pattern.compile("(.*)");
            Matcher nameMatcher = namePattern.matcher(folder.getName());

            //if has prefix
            if (prefixMatcher.find()) {
                dir.setPrefix(prefixMatcher.group(1));
                dir.setName(prefixMatcher.group(2));

                if (parent != null) {
                    //Get separator, only uses first child for simplicity
                    List<File> children = Arrays.asList(Objects.requireNonNull(parentFolder.listFiles(File::isDirectory)));
                    if (!children.isEmpty()) {
                        Pattern separatorPattern = Pattern.compile("([^\\w\\d\\s]|[_+*?^$.])");
                        Matcher separatorMatcher = separatorPattern.matcher(children.get(0).getName());
                        while (separatorMatcher.find()) { //use last found instance of a separator, avoids setting from parent
                            parent.setSeparator(separatorMatcher.group(1));
                        }
                    }
                }
            } else if (nameMatcher.find()) { //else if only has name
                dir.setName(nameMatcher.group(1));
            } else {
                throw new InvalidConfigurationException("Error parsing folder: " + folder.getName()
                        + ". A valid directory name could not be found");
            }

            dir.setChildren(directoriesFromFolder(folder, dir));
            directories.add(dir);
        }

        return directories;
    }
}
