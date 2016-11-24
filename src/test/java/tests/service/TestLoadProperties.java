package tests.service;

import org.testng.annotations.Test;
import sanger.service.PrintConfig;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;

/**
 * @author hc6
 */
public class TestLoadProperties {

    @Test
    public void testLoadProperties() throws IOException {

        PrintConfig result = PrintConfig.loadConfig();

        Map<String, Integer> templateIds = new HashMap<>();
        templateIds.put("d304bc", 6);
        templateIds.put("e367bc", 0);

        assertEquals(result.getPmbURL(), "http://localhost:3000/v1");
        assertEquals(result.getPrinterTemplateIds(), templateIds);

    }
}
