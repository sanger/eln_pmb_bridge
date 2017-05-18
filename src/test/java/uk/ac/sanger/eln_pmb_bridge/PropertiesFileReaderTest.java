package uk.ac.sanger.eln_pmb_bridge;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.*;

import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

/**
 * @author hc6
 */
public class PropertiesFileReaderTest {
    private static final PropertiesFileReader properties = new PropertiesFileReader("test_properties_folder");

    @BeforeMethod
    public void initMethod() throws Exception {
        properties.setProperties();
    }

    @Test
    public void TestSetPropertiesSuccessful(){
        List<String> eln_keys = Arrays.asList("thin_template_id", "error_folder", "archive_folder", "fat_template_id", "poll_folder", "pmb_url");
        assertTrue(properties.getElnPmbProperties().keySet().containsAll(eln_keys));
        List<String> printer_keys = Arrays.asList("123456", "654321");
        assertTrue(properties.getPrinterProperties().keySet().containsAll(printer_keys));
    }

    @Test
    public void TestLoadMailProperties() throws IOException {
        List<String> mail_keys = Arrays.asList("mail.smtp.host", "mail.smtp.port", "to");
        assertTrue(properties.getMailProperties().keySet().containsAll(mail_keys));

        Properties mailProperties = properties.getMailProperties();

        String host = mailProperties.getProperty("mail.smtp.host");
        assertEquals(host, "test_mail.com");

        String port = mailProperties.getProperty("mail.smtp.port");
        assertEquals(port, "9999");

        String to = mailProperties.getProperty("to");
        assertEquals(to, "user@here.com");
    }

    @Test
    public void TestFolderPaths() {
        Properties p = properties.getElnPmbProperties();
        assertEquals(properties.getPollFolder(), p.getProperty("poll_folder"));
        assertEquals(properties.getArchiveFolder(), p.getProperty("archive_folder"));
        assertEquals(properties.getErrorFolder(), p.getProperty("error_folder"));
    }

}
