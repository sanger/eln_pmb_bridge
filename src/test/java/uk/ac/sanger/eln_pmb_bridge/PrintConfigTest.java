package uk.ac.sanger.eln_pmb_bridge;

import org.testng.annotations.Test;

import java.util.*;

import static org.testng.Assert.assertEquals;

/**
 * @author hc6
 */
public class PrintConfigTest {

    @Test
    public void TestLoadConfigFromPropertiesNotSuccessful(){

    }

    @Test
    public void TestLoadConfigFromPropertiesSuccessful() throws Exception {
        PropertiesFileReader fr = new PropertiesFileReader();
        fr.loadProperties();

        PrintConfig result = PrintConfig.loadConfig(fr);
        Map<String, Integer> templateIds = new HashMap<>();
        templateIds.put("d304bc", 15);
        templateIds.put("e367bc", 0);

        assertEquals(result.getPmbURL(), "http://dev.psd.sanger.ac.uk:7462/v1/print_jobs");
        assertEquals(result.getPrinterTemplateIds().get(0), templateIds.get(0));
    }
}
