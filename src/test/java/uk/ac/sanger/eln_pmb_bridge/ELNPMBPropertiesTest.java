package uk.ac.sanger.eln_pmb_bridge;

import org.testng.annotations.Test;

import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

/**
 * Created by hc6 on 04/07/2018.
 */
public class ELNPMBPropertiesTest {
    @Test
    public void TestSetPropertiesSuccessful() throws IOException {
        ELNPMBProperties.setProperties("./test_properties_folder/eln_pmb.properties");

        assertTrue(ELNPMBProperties.getProperties().getClass().equals(Properties.class));
        assertTrue(ELNPMBProperties.getPollFolder().equals("./poll_folder/"));
        assertTrue(ELNPMBProperties.getArchiveFolder().equals("./archive_folder/"));
        assertTrue(ELNPMBProperties.getErrorFolder().equals("./error_folder/"));
        assertTrue(ELNPMBProperties.getPMBURL().equals("http://testurl:123/print_jobs"));
    }

    @Test
    public void TestSetPropertiesMissingPMBURL() throws IOException {
        try {
            ELNPMBProperties.setProperties("./test_properties_folder/eln_pmb_no_url.properties");
        } catch (InvalidPropertiesFormatException e) {
            assertEquals(e.getMessage().trim(), "Cannot load print config because: \n" +
                    "\tPrint My Barcode's url is missing from the properties folder.");
        }
    }

    @Test
    public void TestSetPropertiesMissingTemplateId() throws IOException {
        try {
            ELNPMBProperties.setProperties("./test_properties_folder/eln_pmb_missing_template_id.properties");
        } catch (InvalidPropertiesFormatException e) {
            assertEquals(e.getMessage().trim(), "Cannot load print config because: \n" +
                    "\tThere is at least one template id missing in eln_pmb.properties.");
        }
    }

    @Test
    public void TestSetPropertiesMissingFolder() throws IOException {
        try {
            ELNPMBProperties.setProperties("./test_properties_folder/eln_pmb_missing_folder.properties");
        } catch (InvalidPropertiesFormatException e) {
            assertEquals(e.getMessage().trim(), "Cannot load print config because: \n" +
                    "\tDirectory paths are missing from the eln_pmb.properties");
        }
    }
}
