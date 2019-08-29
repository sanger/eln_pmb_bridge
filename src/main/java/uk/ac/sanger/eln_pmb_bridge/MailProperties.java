package uk.ac.sanger.eln_pmb_bridge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * @author hc6
 */
public class MailProperties extends PropertiesFileReader {
    private static final Logger log = LoggerFactory.getLogger(MailProperties.class);
    private static Properties properties;

    public static void setProperties(String filePath) throws IOException {
        properties = loadFile(filePath);
        log.info("Successfully set mail.properties.");
    }

    public static Properties getProperties() {
        return properties;
    }

    public static String getMailTo() {
        return properties.getProperty("to", "").trim();
    }
}
