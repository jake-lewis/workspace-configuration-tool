package model.configuration;

import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class ConfigurationFactory {

    public static Configuration create() {
        return NullConfiguration.getInstance();
    }

    public static Configuration create(File file) throws IOException {
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



    private static String getExtension(File file) {
        String extension = "";

        int i = file.getName().lastIndexOf('.');
        if (i > 0) {
            extension = file.getName().substring(i+1);
        }

        return extension;
    }
}
