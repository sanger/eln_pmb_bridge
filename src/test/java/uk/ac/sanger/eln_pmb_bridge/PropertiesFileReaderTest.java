package uk.ac.sanger.eln_pmb_bridge;

import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import static org.testng.AssertJUnit.assertTrue;

/**
 * @author hc6
 */
public class PropertiesFileReaderTest {

    @Test(expectedExceptions = FileNotFoundException.class)
    public void TestLoadFileNoFilePath() throws IOException {
        MailProperties.loadFile(null);
    }

    @Test
    public void TestLoadFileForMailProperties() throws IOException {
        Properties props = MailProperties.loadFile("./test_properties_folder/mail.properties");
        List<String> mailKeys = Arrays.asList("mail.smtp.host", "mail.smtp.port", "to");
        assertTrue(props.getClass().equals(Properties.class));
        assertTrue(props.keySet().containsAll(mailKeys));
    }

    @Test
    public void TestLoadFileForPrinterProperties() throws IOException {
        Properties props = PrinterProperties.loadFile("./test_properties_folder/printer.properties");
        List<String> printerKeys = Arrays.asList("123456", "654321");
        assertTrue(props.getClass().equals(Properties.class));
        assertTrue(props.keySet().containsAll(printerKeys));
    }

    @Test
    public void TestLoadFileForELNPMBProperties() throws IOException {
        Properties props = ELNPMBProperties.loadFile("./test_properties_folder/eln_pmb.properties");
        List<String> ELNPMBKeys = Arrays.asList("pmb_url", "poll_folder", "archive_folder", "error_folder", "thin_template_id", "fat_template_id");
        assertTrue(props.getClass().equals(Properties.class));
        assertTrue(props.keySet().containsAll(ELNPMBKeys));
    }


    @Test(expectedExceptions = IOException.class)
    public void TestLoadFileEmptyPropertiesFile() throws IOException {
        MailProperties.loadFile("./test_properties_folder/empty.properties");
    }

}
