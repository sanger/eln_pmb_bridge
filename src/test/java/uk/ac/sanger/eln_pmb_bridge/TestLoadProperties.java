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

        FileManager fm = new FileManager();
        fm.setPMBProperties();
        Properties properties = fm.getPMBProperties();

        String pollFolder = properties.getProperty("poll_folder", "");
        Path pollPath =  fm.getPollFolderPath();
        assertEquals(pollPath, Paths.get(pollFolder));

        PrintConfig result = PrintConfig.loadConfig(fm.getPMBProperties());
        Map<String, Integer> templateIds = new HashMap<>();
        templateIds.put("d304bc", 15);
        templateIds.put("e367bc", 0);

        assertEquals(result.getPmbURL(), "http://dev.psd.sanger.ac.uk:7462/v1/print_jobs");
        assertEquals(result.getPrinterTemplateIds().get(0), templateIds.get(0));
    }


    @Test
    public void testLoadMailProperties() throws IOException {

        FileManager fm = new FileManager();
        Properties properties = fm.getMailProperties();

        String host = properties.getProperty("mail.smtp.host", "");
        assertEquals(host, "mail.sanger.ac.uk");

        String port = properties.getProperty("mail.smtp.port", "");
        assertEquals(port, "25");

        String to = properties.getProperty("to", "");
        assertEquals(to, "hc6@sanger.ac.uk");

    }
}
