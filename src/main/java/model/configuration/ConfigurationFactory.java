package model.configuration;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

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

    public static Configuration create(String text, FileType TYPE) throws IOException, InvalidConfigurationException {
        switch (TYPE) {
            case XML:
                try {
                    return new XMLConfiguration(text);
                } catch (ParseException | ParserConfigurationException | SAXException | TransformerException | IOException e) {
                    e.printStackTrace();
                }
            default: throw new TypeNotPresentException(TYPE.name(), new IllegalArgumentException());
        }
    }

    private static String getExtension(File file) {
        String extension = "";

        int i = file.getName().lastIndexOf('.');
        if (i > 0) {
            extension = file.getName().substring(i+1);
        }

        return extension;
    }
}
