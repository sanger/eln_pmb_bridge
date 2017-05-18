package uk.ac.sanger.eln_pmb_bridge;

import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;

/**
 * @author hc6
 */
public class PrintConfigTest {

    @Test
    public void TestLoadConfigFromPropertiesSuccessful() throws Exception {
        PropertiesFileReader properties = new PropertiesFileReader("test_properties_folder");
        properties.setProperties();

        PrintConfig result = PrintConfig.loadConfig(properties);
        Map<String, Integer> templateIds = new HashMap<>();
        templateIds.put("d304bc", 15);
        templateIds.put("e367bc", 0);

        assertEquals(result.getPmbURL(), "print_job_url");
        assertEquals(result.getPrinterTemplateIds().get(0), templateIds.get(0));
    }
}
