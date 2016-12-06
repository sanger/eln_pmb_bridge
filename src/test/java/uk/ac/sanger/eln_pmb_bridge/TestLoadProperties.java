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
    public void testLoadProperties() throws IOException {

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

        assertEquals(result.getPmbURL(), "http://localhost:3000/v1/print_jobs");
        assertEquals(result.getPrinterTemplateIds(), templateIds);
    }
}
