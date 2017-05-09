package uk.ac.sanger.eln_pmb_bridge;

import org.testng.annotations.Test;

import java.util.Properties;

import static org.testng.Assert.assertEquals;

/**
 * @author hc6
 */
public class EmailServiceTest {
    private static final PropertiesFileReader properties = new PropertiesFileReader();
    protected EmailService emailService;

    @Test
    public void TestGetMailPropertiesSuccessful() throws Exception {
        properties.loadProperties();
        Properties mailProperties = properties.getMailProperties();
        emailService = new EmailService(mailProperties);

        String toAddress = mailProperties.getProperty("to", "").trim();
        assertEquals(toAddress, "hc6@sanger.ac.uk");
    }

    @Test
    public void TestSendEmailSuccessful() throws Exception {
    }

    @Test
    public void TestSendEmailNotSuccessful(){

    }
}
