package uk.ac.sanger.eln_pmb_bridge;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

/**
 * @author hc6
 */
public class PrintRequestHelperTest {
    private static final PropertiesFileReader properties = new PropertiesFileReader("test_properties_folder");
    private PrintRequestHelper mockPrintRequestHelper;

    @BeforeMethod
    public void initMethod() throws Exception {
        properties.setProperties();
        mockPrintRequestHelper = mock(PrintRequestHelper.class);
        mockPrintRequestHelper.printerProperties = properties.getPrinterProperties();
        when(mockPrintRequestHelper.makeRequestFromFile(any())).thenCallRealMethod();

    }

    @Test
    public void TestPrinterPropertiesSuccessful() throws Exception {
        PrintRequestHelper helper = new PrintRequestHelper(properties.getPrinterProperties());
        List<String> printer_keys = Arrays.asList("123456", "654321");
        assertTrue(helper.printerProperties.keySet().containsAll(printer_keys));
    }

    @Test
    public void TestFileNameDoesNotExist() throws Exception {
        Path path = Paths.get("");
        try {
            path = Paths.get("./test_examples/xxx.txt");
            mockPrintRequestHelper.makeRequestFromFile(path);
        } catch (NullPointerException e){
            assertEquals(e.getMessage().trim(), path.toString()+" does not exist");
        }
    }

    @Test
    public void TestPrinterListIsEmpty() throws Exception {

        try {
            when(mockPrintRequestHelper.getPrinterList(any())).thenReturn(Collections.emptyList());
            Path path = Paths.get("./test_examples/correct_request.txt");
            mockPrintRequestHelper.makeRequestFromFile(path);
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Cannot make print request because printer list is empty.");
        }
    }

    @Test
    public void TestPrinterNameDoesNotExistInFile() throws IOException {
        when(mockPrintRequestHelper.getPrinterName(any())).thenCallRealMethod();

        Path path = Paths.get("./test_examples/no_printer_name.txt");
        Scanner expectedData = new Scanner(path);
        String firstLine = expectedData.nextLine();

        try {
            mockPrintRequestHelper.getPrinterName(firstLine);
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "No printer name is given in print request");
        }
    }

    @Test
    public void TestPrinterNameDoesNotExistInGivenListOfPrinters() throws IOException {
        try {
            when(mockPrintRequestHelper.createLabels(any())).thenCallRealMethod();
            when(mockPrintRequestHelper.getPrinterList(any())).thenCallRealMethod();
            Path path = Paths.get("./test_examples/unknown_printer.txt");
            mockPrintRequestHelper.makeRequestFromFile(path);
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Cannot make print request because: Printer name null does not exist.");
        }
    }

    @Test
    public void TestLabelListIsEmpty() throws IOException {
        try {
            when(mockPrintRequestHelper.getPrinterName(any())).thenCallRealMethod();
            when(mockPrintRequestHelper.getPrinterList(any())).thenCallRealMethod();
            Path path = Paths.get("./test_examples/no_requests.txt");
            mockPrintRequestHelper.makeRequestFromFile(path);
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Cannot make print request because: Label list is empty.");
        }
    }

    @Test
    public void TestCreatingLabels() throws IOException {
        when(mockPrintRequestHelper.createLabels(any())).thenCallRealMethod();

        Path path = Paths.get("./test_examples/correct_request.txt");

        Scanner expectedData = new Scanner(path);
        expectedData.nextLine();
        expectedData.nextLine();
        String[] expectedColumns = expectedData.nextLine().split("\\||,");

        int numOfRequestedLabels = 0;
        while(expectedData.hasNext()) {
            expectedData.nextLine();
            numOfRequestedLabels++;
        }

        List<String> columns = new ArrayList<>();
        for (String col : expectedColumns) {
            col = col.trim().toLowerCase().replaceAll("\\s+", "_");
            columns.add(col);
            if (col.equals("barcode")) {
                columns.add(col+"_text");
            }
        }

        Scanner actualData = new Scanner(path);
        actualData.nextLine();
        actualData.nextLine();
        List<PrintRequest.Label> labels = mockPrintRequestHelper.createLabels(actualData);

//       expect the field names to be created from the given file's column headings on the fly
        for (PrintRequest.Label label : labels) {
            assertTrue(label.getFields().keySet().containsAll(columns));
        }
//        expect the number of labels created to be correct
        assertEquals(labels.size(), numOfRequestedLabels);
    }

    @Test
    public void TestMakeRequestFromFile() throws Exception {
        when(mockPrintRequestHelper.getPrinterList(any())).thenCallRealMethod();
        when(mockPrintRequestHelper.createLabels(any())).thenCallRealMethod();
        when(mockPrintRequestHelper.getPrinterName(any())).thenCallRealMethod();

        Path path = Paths.get("./test_examples/correct_request.txt");
        PrintRequest request = mockPrintRequestHelper.makeRequestFromFile(path);

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
