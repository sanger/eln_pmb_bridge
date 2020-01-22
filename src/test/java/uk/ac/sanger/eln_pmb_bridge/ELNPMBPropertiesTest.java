package uk.ac.sanger.eln_pmb_bridge;

import org.testng.annotations.Test;

import java.io.IOException;
import java.util.InvalidPropertiesFormatException;

import static org.testng.Assert.*;

/**
 *  @author hc6
 */
public class ELNPMBPropertiesTest {
    @Test
    public void TestSetPropertiesSuccessful() throws IOException {
        ELNPMBProperties.setProperties("./test_properties_folder/eln_pmb.properties");

        assertNotNull(ELNPMBProperties.getProperties());
        assertEquals(ELNPMBProperties.getPollFolder(), "./poll_folder/");
        assertEquals(ELNPMBProperties.getArchiveFolder(), "./archive_folder/");
        assertEquals(ELNPMBProperties.getErrorFolder(), "./error_folder/");
        assertEquals(ELNPMBProperties.getPMBURL(), "http://testurl:123/print_jobs");
    }

    @Test
    public void TestSetPropertiesMissingPMBURL() throws IOException {
        try {
            ELNPMBProperties.setProperties("./test_properties_folder/eln_pmb_no_url.properties");
            fail("An exception should have been thrown.");
        } catch (InvalidPropertiesFormatException e) {
            assertEquals(e.getMessage().trim(), "Cannot load print config because: \n" +
                    "\tPrint My Barcode's url is missing from the properties folder.");
        }
    }

    @Test
    public void TestSetPropertiesMissingTemplateId() throws IOException {
        try {
            ELNPMBProperties.setProperties("./test_properties_folder/eln_pmb_missing_template_id.properties");
            fail("An exception should have been thrown.");
        } catch (InvalidPropertiesFormatException e) {
            assertEquals(e.getMessage().trim(), "Cannot load print config because: \n" +
                    "\tThere is at least one template id missing in eln_pmb.properties.");
        }
    }

    @Test
    public void TestSetPropertiesMissingFolder() throws IOException {
        try {
            ELNPMBProperties.setProperties("./test_properties_folder/eln_pmb_missing_folder.properties");
            fail("An exception should have been thrown.");
        } catch (InvalidPropertiesFormatException e) {
            assertEquals(e.getMessage().trim(), "Cannot load print config because: \n" +
                    "\tDirectory paths are missing from the eln_pmb.properties");
        }
    }
}
