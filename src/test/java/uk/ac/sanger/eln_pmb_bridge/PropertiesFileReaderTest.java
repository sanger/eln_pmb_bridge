package uk.ac.sanger.eln_pmb_bridge;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.*;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

/**
 * @author hc6
 */
public class PropertiesFileReaderTest {
    private static final PropertiesFileReader properties = new PropertiesFileReader();

    @BeforeMethod
    public void initMethod() throws Exception {
        properties.loadProperties();
    }

    @Test
    public void TestLoadPropertiesOnStart(){
        List<String> eln_keys = Arrays.asList("thin_template_id", "error_folder", "archive_folder", "fat_template_id", "poll_folder", "pmb_url");
        assertTrue(properties.getElnPmbProperties().keySet().containsAll(eln_keys));
        assertTrue(properties.getPrinterProperties().keySet().size()>1);
    }

    @Test
    public void TestLoadMailProperties() throws IOException {
        List<String> mail_keys = Arrays.asList("mail.smtp.host", "mail.smtp.port", "to");
        assertTrue(properties.getMailProperties().keySet().containsAll(mail_keys));

        Properties mailProperties = properties.getMailProperties();

        String host = mailProperties.getProperty("mail.smtp.host", "");
        assertEquals(host, "mail.sanger.ac.uk");

        String port = mailProperties.getProperty("mail.smtp.port", "");
        assertEquals(port, "25");
    }

    @Test
    public void TestFolderPaths() {
        Properties p = properties.getElnPmbProperties();
        assertEquals(properties.getPollFolderPath(), Paths.get(p.getProperty("poll_folder")));
        assertEquals(properties.getArchiveFolder(), p.getProperty("archive_folder"));
        assertEquals(properties.getErrorFolder(), p.getProperty("error_folder"));
    }

    @Test
    public void TestGetPrinters(){
        assertTrue(properties.getPrinters().contains("d304bc"));
    }

    @Test
    public void TestFindPropertiesFileSuccessful() throws FileNotFoundException {
        File file = new File("./properties_folder/eln_pmb.properties");
        assertEquals(file, properties.findPropertiesFile("eln_pmb.properties"));
    }

    @Test
    public void TestFindPropertiesFileNotSuccessful(){
        try {
            properties.findPropertiesFile("doesnt_exist.properties");
        } catch (FileNotFoundException e) {
            assertEquals(e.getMessage(), "Missing properties file doesnt_exist.properties.");
        }
    }

    @Test
    public void TestFindFileSuccessful() throws IOException {
        File file = new File("/Users/hc6/eln_pmb_bridge/poll_folder/test.txt");
        file.createNewFile();
        assertEquals(file, properties.findFile("test.txt"));
    }

    @Test
    public void TestFindFileNotSuccessful(){
        try {
            properties.findFile("doesnt_exist.txt");
        } catch (FileNotFoundException e) {
            assertEquals(e.getMessage(), "File doesnt_exist.txt does not exist");
        }
    }

    @Test
    public void TestMoveFileToFolderSuccessful() throws IOException {
        File file = new File("/Users/hc6/eln_pmb_bridge/data_test/move_test.txt");
        file.createNewFile();

        properties.moveFileToFolder("move_test.txt", properties.getArchiveFolder());
        String time = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

        String newName = "move_test_"+time+".txt";
        File newFile = new File("/Users/hc6/eln_pmb_bridge/archive_folder/"+newName);
        assertEquals(newFile, properties.findFile(newName));

    }

    @Test
    public void TestMoveFileToFolderNotSuccessful() throws IOException {
//        check file has write permissions to move
    }

    @Test
    public void TestMoveFileWithBadName() throws IOException {
//        check file has write permissions to move
    }

}
