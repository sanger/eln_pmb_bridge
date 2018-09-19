package uk.ac.sanger.eln_pmb_bridge;

import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Properties;

import static org.testng.AssertJUnit.assertTrue;

/**
 * @author hc6
 */
public class MailPropertiesTest {
    @Test
    public void TestSetPropertiesSuccessful() throws IOException {
        MailProperties.setProperties("./test_properties_folder/mail.properties");

        assertTrue(MailProperties.getProperties().getClass().equals(Properties.class));
        assertTrue(MailProperties.getMailTo().equals("user@here.com"));

    }
}

