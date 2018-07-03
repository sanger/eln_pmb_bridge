
package uk.ac.sanger.eln_pmb_bridge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.*;

/**
 * An abstract Properties class, with instances such as MailProperties and PrinterProperties
 * each calling loadFile, to read their respective property files.
 * @author hc6
 */
public abstract class PropertiesFileReader {
    private static final Logger log = LoggerFactory.getLogger(PrintRequestHelper.class);

    public static Properties loadFile(String propertyFilePath) throws IOException {
        if (propertyFilePath == null) {
            throw new NullPointerException(ErrorType.MISSING_PROP_FILE.getMessage());
        }
        Path propertyFile = Paths.get("./properties_folder"+propertyFilePath);
        Properties properties = new Properties();
        try {
            InputStream propertyFileInputStream = Files.newInputStream(propertyFile);
            properties.load(propertyFileInputStream);
        }  catch (Exception e) {
            log.debug("throw", e);
            throw new IOException("TODO", e);
        }
        return properties;
    }
}
