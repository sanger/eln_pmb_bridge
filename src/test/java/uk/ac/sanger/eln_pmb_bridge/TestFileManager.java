package uk.ac.sanger.eln_pmb_bridge;

import org.testng.annotations.Test;
import java.io.IOException;
import java.util.Properties;

import static org.testng.Assert.assertEquals;

/**
 * @author hc6
 */
public class TestFileManager {

    @Test
    public void testReadPropertiesFromFile() throws IOException {
        FileManager fileManager = new FileManager();
        fileManager.setPMBProperties();
        Properties resultProperties = fileManager.getPMBProperties();

        assertEquals(resultProperties.getProperty("pmb_url"), "http://localhost:3000/v1/print_jobs");
        assertEquals(resultProperties.getProperty("d304bc"), "6");
    }

    @Test
    public void testMakeRequestFromFile() throws IOException {
        FileManager fileManager = new FileManager();
        fileManager.setPMBProperties();
        PrintRequest request = fileManager.makeRequestFromFile("test_print_request.csv");

        assertEquals(request.getLabels().get(0).getField("cell_line"), "nawk");
        assertEquals(request.getLabels().get(0).getField("barcode"), "200000000101");
    }

    @Test
    public void testArchiveFile() throws IOException {
        FileManager fileManager = new FileManager();
        fileManager.setPMBProperties();

        Properties resultProperties = fileManager.getPMBProperties();
        assertEquals(resultProperties.getProperty("archive_folder"), "/Users/hc6/Desktop/eln_pmb_folder/archive_folder");
    }

}
