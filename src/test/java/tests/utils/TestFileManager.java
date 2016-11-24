package tests.utils;

import org.testng.annotations.Test;
import sanger.utils.FileManager;
import sanger.parameters.PrintRequest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import static org.testng.Assert.assertEquals;

/**
 * @author hc6
 */
public class TestFileManager {

    @Test
    public void testReadPropertiesFromFile() throws FileNotFoundException {
        FileManager fileManager = new FileManager();
        Properties resultProperties = fileManager.readPropertiesFile("test_pmb.properties");

        assertEquals(resultProperties.getProperty("pmb_url"), "url");
        assertEquals(resultProperties.getProperty("d304bc"), "1");
    }

    @Test
    public void testMakeRequestFromFile() throws IOException {
        FileManager fileManager = new FileManager();
        PrintRequest request = fileManager.makeRequestFromFile("test_print_request.csv");

        assertEquals(request.getLabels().get(0).getField("cell_line"), "nawk");
        assertEquals(request.getLabels().get(0).getField("barcode"), "200000000101");
    }

    @Test
    public void testReadPrintersFromFile() throws IOException {
        FileManager fileManager = new FileManager();
        List<String> result = fileManager.getPrintersFromFile("test_printers.properties");
        List<String> printers = Arrays.asList("d304bc","e367bc");
        assertEquals(result, printers);
    }

}
