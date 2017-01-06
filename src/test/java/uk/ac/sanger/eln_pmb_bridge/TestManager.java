package uk.ac.sanger.eln_pmb_bridge;

import org.testng.annotations.Test;

import java.io.File;
import java.util.Collections;
import java.util.Properties;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author hc6
 */
public class TestManager {

    @Test
    public void testReadPropertiesFromFile() throws Exception {
        PropertiesFileReader fileReader = new PropertiesFileReader();
        fileReader.loadProperties();
        Properties resultProperties = fileReader.getElnPmbProperties();

        assertEquals(resultProperties.getProperty("pmb_url"), "http://dev.psd.sanger.ac.uk:7462/v1/print_jobs");
        assertTrue(resultProperties.containsKey("poll_folder"));
        assertTrue(resultProperties.containsKey("archive_folder"));
    }

    @Test
    public void testMakeRequestFromFile() throws Exception {
        PrintRequestHelper printRequestHelper = new PrintRequestHelper();
        PropertiesFileReader fileReader = new PropertiesFileReader();

        fileReader.loadProperties();
        File newFile = new File(System.getProperty("user.dir")
                + File.separator + "data" + File.separator + "test_print_request.txt");

        PrintRequest request = printRequestHelper.makeRequestFromFile(newFile, Collections.singletonList("d304bc"));

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
