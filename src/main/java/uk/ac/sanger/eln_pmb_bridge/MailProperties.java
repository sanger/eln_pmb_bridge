package uk.ac.sanger.eln_pmb_bridge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by hc6 on 02/07/2018.
 */
public class MailProperties extends PropertiesFileReader {
    private static final Logger log = LoggerFactory.getLogger(PrintRequestHelper.class);
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
