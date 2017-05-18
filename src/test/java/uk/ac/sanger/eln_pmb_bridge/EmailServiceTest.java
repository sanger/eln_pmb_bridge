package uk.ac.sanger.eln_pmb_bridge;

import org.testng.annotations.Test;

import java.util.Properties;

import static org.testng.Assert.assertEquals;

/**
 * @author hc6
 */
public class EmailServiceTest {
    private static final PropertiesFileReader properties = new PropertiesFileReader("test_properties_folder");
    protected EmailService emailService;

    @Test
    public void TestGetMailPropertiesSuccessful() throws Exception {
        properties.setProperties();
        Properties mailProperties = properties.getMailProperties();
        emailService = new EmailService(mailProperties);

        String toAddress = mailProperties.getProperty("to", "").trim();
        assertEquals(toAddress, "user@here.com");
    }

    @Test
    public void TestSendEmailSuccessful() throws Exception {
    }

    @Test
    public void TestSendEmailNotSuccessful(){

    }
}
