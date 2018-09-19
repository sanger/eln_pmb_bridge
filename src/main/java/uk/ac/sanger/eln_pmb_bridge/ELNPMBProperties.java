package uk.ac.sanger.eln_pmb_bridge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

/**
 * @author hc6
 */
public class ELNPMBProperties extends PropertiesFileReader {
    private static final Logger log = LoggerFactory.getLogger(PrintRequestHelper.class);
    private static Properties properties;
    private static String PMBURL;

    public static void setProperties(String filePath) throws IOException {
        properties = loadFile(filePath);
        checkProps();
        log.info("Successfully set eln_pmb.properties.");
    }

    private static void checkProps() throws InvalidPropertiesFormatException {
        PMBURL = properties.getProperty("pmb_url", "");
        String thin_template_id = properties.getProperty("thin_template_id", "");
        String fat_template_id = properties.getProperty("fat_template_id", "");

        String message = "";
        if (PMBURL.isEmpty()) {
            message += "\n\t"+ ErrorType.MISSING_PMB_URL.getMessage();
        }
        if (thin_template_id.isEmpty() || fat_template_id.isEmpty()) {
            message += "\n\t"+ ErrorType.NO_TEMPLATE_ID.getMessage();
        }

        if (getPollFolder().isEmpty() || getArchiveFolder().isEmpty() || getErrorFolder().isEmpty() ) {
            message += "\n\t"+ ErrorType.MISSING_DIR_PATHS.getMessage();
        }

        if (!message.isEmpty()) {
            String msg = String.format("Cannot load print config because: %s", message);
            throw new InvalidPropertiesFormatException(msg);
        }
    }

    public static String getPollFolder() {
        return properties.getProperty("poll_folder", "");
    }

    public static String getArchiveFolder() {
        return properties.getProperty("archive_folder", "");
    }

    public static String getErrorFolder() {
        return properties.getProperty("error_folder", "");
    }

    public static Properties getProperties() { return properties; }

    public static String getPMBURL() {
        return PMBURL;
    }

}
