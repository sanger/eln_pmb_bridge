package uk.ac.sanger.eln_pmb_bridge;

import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;
import static org.testng.AssertJUnit.assertTrue;

/**
 * @author hc6
 */
public class PropertiesFileReaderTest {

    @Test
    public void TestLoadFileNoFilePath() throws IOException {
        try {
            MailProperties.loadFile(null);
            fail("An exception should have been thrown.");
        } catch (FileNotFoundException e) {
            assertEquals(e.getMessage().trim(), "Property file path is missing.");
        }
    }

    @Test
    public void TestLoadFileEmptyPropertiesFile() throws IOException {
        try {
            MailProperties.loadFile("./test_properties_folder/empty.properties");
            fail("An exception should have been thrown.");
        } catch (IOException e) {
            assertEquals(e.getMessage().trim(), "The property file is empty.");
        }

    }

    @Test
    public void TestLoadFileForMailProperties() throws IOException {
        Properties props = MailProperties.loadFile("./test_properties_folder/mail.properties");
        List<String> mailKeys = Arrays.asList("mail.smtp.host", "mail.smtp.port", "to");
        assertTrue(props.keySet().containsAll(mailKeys));
    }

    @Test
    public void TestLoadFileForELNPMBProperties() throws IOException {
        Properties props = ELNPMBProperties.loadFile("./test_properties_folder/eln_pmb.properties");
        List<String> ELNPMBKeys = Arrays.asList("pmb_url", "poll_folder", "archive_folder", "error_folder", "thin_template_id", "fat_template_id");
        assertTrue(props.keySet().containsAll(ELNPMBKeys));
    }

}
