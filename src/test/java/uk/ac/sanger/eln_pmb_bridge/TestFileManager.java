package uk.ac.sanger.eln_pmb_bridge;

import org.testng.annotations.Test;

import java.util.Properties;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author hc6
 */
public class TestFileManager {

    @Test
    public void testReadPropertiesFromFile() throws Exception {
        FileManager fileManager = new FileManager();
        fileManager.setPMBProperties();
        Properties resultProperties = fileManager.getPMBProperties();

        assertEquals(resultProperties.getProperty("pmb_url"), "http://localhost:3000/v1/print_jobs");
        assertTrue(resultProperties.containsKey("poll_folder"));
        assertTrue(resultProperties.containsKey("archive_folder"));

        for (String printer : fileManager.getPrinters()){
            assertTrue(resultProperties.containsKey(printer));
        }

    }

    @Test
    public void testMakeRequestFromFile() throws Exception {
        FileManager fileManager = new FileManager();
        fileManager.setPMBProperties();
        PrintRequest request = fileManager.makeRequestFromFile("test_print_request.txt");

        assertEquals(request.getLabels().get(0).getField("cell_line"), "nawk");
        assertEquals(request.getLabels().get(0).getField("barcode"), "200000000111");
        assertEquals(request.getLabels().get(0).getField("barcode_text"), "200000000111");
        assertEquals(request.getLabels().get(0).getField("passage_number"), "3");
        assertEquals(request.getLabels().get(0).getField("date"), "2016-10-17");

        assertEquals(request.getLabels().get(1).getField("cell_line"), "zogh");
        assertEquals(request.getLabels().get(1).getField("barcode"), "200000000222");
        assertEquals(request.getLabels().get(1).getField("barcode_text"), "200000000222");
        assertEquals(request.getLabels().get(1).getField("passage_number"), "4");
        assertEquals(request.getLabels().get(1).getField("date"), "2015-12-06");
    }

}
