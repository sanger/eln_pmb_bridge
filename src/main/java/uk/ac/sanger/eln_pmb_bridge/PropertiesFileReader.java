
package uk.ac.sanger.eln_pmb_bridge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Properties;

/**
 * Properties helper class to
 *   - find properties files
 *   - set properties files
 *   - get properties
 * @author hc6
 */
public class PropertiesFileReader {
    private static final Logger log = LoggerFactory.getLogger(PrintRequestHelper.class);
    private Properties elnPmbProperties;
    private Properties printerProperties;


    protected void setProperties() throws IOException {
        Path elnPmbPropertiesFile = Paths.get("./properties_folder/eln_pmb.properties");
        InputStream elnPmbFileInputStream = Files.newInputStream(elnPmbPropertiesFile);
        this.elnPmbProperties = new Properties();
        this.elnPmbProperties.load(elnPmbFileInputStream);

        Path printerPropertiesFile = Paths.get("./properties_folder/printer.properties");
        InputStream printerFileInputStream = Files.newInputStream(printerPropertiesFile);
        this.printerProperties = new Properties();
        this.printerProperties.load(printerFileInputStream);

        log.info("Successfully set eln_pmb.properties");
    }

    protected Properties getElnPmbProperties() {
        return this.elnPmbProperties;
    }

    protected Properties getPrinterProperties(){
        return this.printerProperties;
    }

    /**
     * Mail properties can be updated while service is running
     */
    protected Properties getMailProperties() throws IOException {
        Path mailPropertiesFile = Paths.get("./properties_folder/mail.properties");
        InputStream mailFileInputStream = Files.newInputStream(mailPropertiesFile);
        Properties mailProperties = new Properties();
        mailProperties.load(mailFileInputStream);
        return mailProperties;
    }

    protected String getPollFolder() {
        return elnPmbProperties.getProperty("poll_folder");
    }

    protected String getArchiveFolder() {
        return elnPmbProperties.getProperty("archive_folder");
    }

    protected String getErrorFolder() {
        return elnPmbProperties.getProperty("error_folder");
    }

}
