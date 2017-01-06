package uk.ac.sanger.eln_pmb_bridge;

import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.testng.Assert.assertEquals;

/**
 * @author hc6
 */
public class TestLoadProperties {

    @Test
    public void testLoadPMBProperties() throws Exception {

        PropertiesFileReader fr = new PropertiesFileReader();
        fr.loadProperties();
        Properties properties = fr.getElnPmbProperties();

        String pollFolder = properties.getProperty("poll_folder", "");
        Path pollPath =  fr.getPollFolderPath();
        assertEquals(pollPath, Paths.get(pollFolder));

        PrintConfig result = PrintConfig.loadConfig(fr.getElnPmbProperties(), fr.getPrinterProperties());
        Map<String, Integer> templateIds = new HashMap<>();
        templateIds.put("d304bc", 15);
        templateIds.put("e367bc", 0);

        assertEquals(result.getPmbURL(), "http://dev.psd.sanger.ac.uk:7462/v1/print_jobs");
        assertEquals(result.getPrinterTemplateIds().get(0), templateIds.get(0));
    }


    @Test
    public void testLoadMailProperties() throws IOException {

        PropertiesFileReader fr = new PropertiesFileReader();
        Properties properties = fr.getMailProperties();

        String host = properties.getProperty("mail.smtp.host", "");
        assertEquals(host, "mail.sanger.ac.uk");

        String port = properties.getProperty("mail.smtp.port", "");
        assertEquals(port, "25");

        String to = properties.getProperty("to", "");
        assertEquals(to, "hc6@sanger.ac.uk");

    }
}
