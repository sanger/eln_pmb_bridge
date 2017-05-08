package uk.ac.sanger.eln_pmb_bridge;

import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import static org.testng.Assert.assertEquals;

/**
 * @author hc6
 */
public class PropertiesFileReaderTest {

    @Test
    public void TestLoadELNPMBFolderProperties() throws Exception {
        PropertiesFileReader fr = new PropertiesFileReader();
        fr.loadProperties();
        Properties properties = fr.getElnPmbProperties();

        String pollFolder = properties.getProperty("poll_folder", "");
        Path pollPath =  fr.getPollFolderPath();
        assertEquals(pollPath, Paths.get(pollFolder));
    }

    @Test
    public void TestLoadPrintProperties() {

    }

    @Test
    public void TestLoadMailProperties() throws IOException {

        PropertiesFileReader fr = new PropertiesFileReader();
        Properties properties = fr.getMailProperties();

        String host = properties.getProperty("mail.smtp.host", "");
        assertEquals(host, "mail.sanger.ac.uk");

        String port = properties.getProperty("mail.smtp.port", "");
        assertEquals(port, "25");

        String to = properties.getProperty("to", "");
        assertEquals(to, "hc6@sanger.ac.uk");

    }

    @Test
    public void TestFindFileSuccessful(){

    }

    @Test
    public void TestFindFileNotSuccessful(){

    }

    @Test
    public void TestMoveFileToFolderSuccessful(){

    }

    @Test
    public void TestMoveFileToFolderNotSuccessful(){

    }
}
