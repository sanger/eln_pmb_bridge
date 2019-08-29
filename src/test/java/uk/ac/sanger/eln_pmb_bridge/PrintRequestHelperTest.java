package uk.ac.sanger.eln_pmb_bridge;

import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

/**
 * @author hc6
 */
public class PrintRequestHelperTest {

    public void setProperties() throws IOException {
        ELNPMBProperties.setProperties("./test_properties_folder/eln_pmb.properties");
        PrinterConfig.initialise("./test_properties_folder/printer.properties");
    }

    @Test
    public void TestPrinterListEmpty() throws Exception {
        try {
            new PrintRequestHelper();
        } catch (NullPointerException e) {
            assertEquals(e.getMessage().trim(),
                    "Cannot make print request because: The list of printers is empty in the properties folder.");
        }
    }

    @Test
    public void TestFileNameDoesNotExist() throws Exception {
        setProperties();
        Path path = Paths.get("xx");
        PrintRequestHelper helper = new PrintRequestHelper();

        try {
            helper.makeRequestFromFile(path);
        } catch (FileNotFoundException e){
            assertEquals(e.getMessage().trim(), path.toString()+" does not exist");
        }
    }

    @Test
    public void TestPrinterListIsEmpty() throws Exception {
        setProperties();
        Path path = Paths.get("./test_examples/unknown_printer.txt");
        PrintRequestHelper helper = new PrintRequestHelper();

        try {
            helper.makeRequestFromFile(path);
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Cannot make print request because: \n" +
                    "\txxx:The printer name does not exist. ");
        }
    }

    @Test
    public void TestGetPrinterName() throws IOException {
        setProperties();

        Path path = Paths.get("./test_examples/correct_request_test.txt");
        Scanner expectedData = new Scanner(path);
        String firstLine = expectedData.nextLine();

        PrintRequestHelper helper = new PrintRequestHelper();
        String printerName = helper.getPrinterName(firstLine);
        assertEquals(printerName, "123456");
    }

    @Test
    public void TestGetNumberOfCopies() throws IOException {
        setProperties();

        Path path = Paths.get("./test_examples/correct_request_test_multiple_copies.txt");
        Scanner expectedData = new Scanner(path);
        String firstLine = expectedData.nextLine();

        PrintRequestHelper helper = new PrintRequestHelper();
        int numOfCopies= helper.getNumberOfCopies(firstLine);
        assertEquals(numOfCopies, 3);
    }

    @Test
    public void TestGetPrinterNameDoesNotExist() throws IOException {
        setProperties();

        Path path = Paths.get("./test_examples/no_printer_name.txt");
        Scanner expectedData = new Scanner(path);
        String firstLine = expectedData.nextLine();

        PrintRequestHelper helper = new PrintRequestHelper();
        try {
            helper.getPrinterName(firstLine);
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "There is no printer name in the request. ");
        }
    }

    @Test
    public void TestLabelListIsEmpty() throws IOException {
        setProperties();
        PrintRequestHelper helper = new PrintRequestHelper();
        Path path = Paths.get("./test_examples/no_requests.txt");

        try {
            helper.makeRequestFromFile(path);
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Cannot make print request because: \n" +
                    "\tThere are no labels to be printed in the request. ");
        }
    }

    @Test
    public void TestCreatingLabels() throws IOException {
        setProperties();

        PrintRequestHelper helper = new PrintRequestHelper();
        Path path = Paths.get("./test_examples/correct_request.txt");

        Scanner expectedData = new Scanner(path);
        expectedData.nextLine();
        expectedData.nextLine();
        String[] expectedColumns = expectedData.nextLine().split("[|,]");

        int numOfRequestedLabels = 0;
        while(expectedData.hasNext()) {
            expectedData.nextLine();
            numOfRequestedLabels++;
        }

        List<String> columns = new ArrayList<>();
        for (String col : expectedColumns) {
            col = col.trim().toLowerCase().replaceAll("\\s+", "_");
            columns.add(col);
        }

        Scanner actualData = new Scanner(path);
        actualData.nextLine();
        actualData.nextLine();
        List<PrintRequest.Label> labels = helper.createLabels(actualData);

        for (PrintRequest.Label label : labels) {
            assertTrue(label.getFields().keySet().containsAll(columns));
        }
        assertEquals(labels.size(), numOfRequestedLabels);
    }

    @Test
    public void TestMakeRequestFromFile() throws Exception {
        setProperties();

        PrintRequestHelper helper = new PrintRequestHelper();
        Path path = Paths.get("./test_examples/correct_request_test.txt");
        PrintRequest request = helper.makeRequestFromFile(path);

        assertEquals(request.getPrinterName(), "123456");
        assertEquals(request.getNumOfCopies(), 1);
        assertEquals(request.getLabels().size(), 2);

        assertEquals(request.getLabels().get(0).getField("cell_line"), "nawk");
        assertEquals(request.getLabels().get(0).getField("passage_number"), "3");
        assertEquals(request.getLabels().get(0).getField("date"), "22 January 2018");

        assertEquals(request.getLabels().get(1).getField("cell_line"), "zogh");
        assertEquals(request.getLabels().get(1).getField("passage_number"), "4");
        assertEquals(request.getLabels().get(1).getField("date"), "22 January 2019");
    }

}
