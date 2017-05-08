package uk.ac.sanger.eln_pmb_bridge;

import org.testng.annotations.Test;

import java.io.File;
import java.util.Collections;

import static org.testng.Assert.assertEquals;

/**
 * @author hc6
 */
public class PrintRequestHelperTest {

    @Test
    public void TestFileNameDoesNotExist(){

    }

    @Test
    public void TestFileNameIsBadFormat(){

    }

    @Test
    public void TestPrinterListIsEmpty(){

    }

    @Test
    public void TestPrinterNameDoesNotExistInGivenListOfPrinters(){

    }

    @Test
    public void TestCreatingLabels(){

    }

    @Test
    public void TestMakeRequestFromFile() throws Exception {
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
