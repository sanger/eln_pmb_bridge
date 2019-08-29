package uk.ac.sanger.eln_pmb_bridge;

import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Collections;

import static org.testng.Assert.*;

/**
 * @author dr6
 */
public class SPrintConfigTest {
    @Test
    public void testSPrintConfig() throws IOException {
        SPrintConfig.initialise("./test_properties_folder/sprint.properties");
        SPrintConfig sc = SPrintConfig.getInstance();

        assertEquals(sc.getHost(), "sprint_url/graphiql");

        assertEquals(sc.getTemplate(LabelType.thin).substitute(Collections.singletonMap("barcode", "CGAP-123")),
                "{ \"barcode\":  \"CGAP-123\" }");

        try {
            sc.getTemplate(LabelType.fat);
            fail("Expected an exception");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("fat"));
        }
    }
}
