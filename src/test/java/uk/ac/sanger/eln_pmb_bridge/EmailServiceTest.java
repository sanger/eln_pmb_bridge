package uk.ac.sanger.eln_pmb_bridge;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;
/**
 * @author hc6
 */
public class EmailServiceTest {

    @BeforeClass
    public void setUp() throws IOException {
        MailProperties.setProperties("./test_properties_folder/mail.properties");
    }

    @Test
    public void TestGetMailPropertiesSuccessful() throws Exception {
        String toAddress = MailProperties.getMailTo().trim();
        assertEquals(toAddress, "user@here.com");
    }
}