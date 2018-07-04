package uk.ac.sanger.eln_pmb_bridge;

import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Properties;

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
        assertTrue(ELNPMBProperties.getPMBURL().equals("print_job_url"));
    }
}
