package uk.ac.sanger.eln_pmb_bridge;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

/**
 * @author hc6
 */
public class PrintRequestHelperTest {
    private static final PropertiesFileReader properties = new PropertiesFileReader();
    private PrintRequestHelper printRequestHelper = new PrintRequestHelper();
    private List<String> printerList;

    @BeforeMethod
    public void initMethod() throws Exception {
        properties.loadProperties();
        printerList = Arrays.asList("d304bc", "e367bc");
    }

    @Test
    public void TestFileNameDoesNotExist() throws Exception {
        File emptyFile = new File("");

        try {
            printRequestHelper.makeRequestFromFile(emptyFile, printerList);
        } catch (FileNotFoundException e){
            assertEquals(e.getMessage().trim(), "(No such file or directory)");
        }
    }

    @Test
    public void TestPrinterListIsEmpty() throws Exception {
        File file = properties.findFile("correct_request.txt");

        try {
            printRequestHelper.makeRequestFromFile(file, Collections.emptyList());
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Cannot make print request because printer list is empty.");
        }
    }

    @Test
    public void TestPrinterNameDoesNotExistInFile() throws FileNotFoundException {
        File file = properties.findFile("no_printer_name.txt");

        Scanner expectedData = new Scanner(file);
        String firstLine = expectedData.nextLine();

        try {
            printRequestHelper.getPrinterName(firstLine);
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "No printer name is given in print request");
        }
    }

    @Test
    public void TestPrinterNameDoesNotExistInGivenListOfPrinters() throws FileNotFoundException {
        File file = properties.findFile("unknown_printer.txt");

        try {
            printRequestHelper.makeRequestFromFile(file, printerList);
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Cannot make print request because printer name xxx doesn't exist.");
        }
    }

    @Test
    public void TestLabelListIsEmpty() throws FileNotFoundException {
        File file = properties.findFile("no_requests.txt");

        try {
            printRequestHelper.makeRequestFromFile(file, printerList);
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Cannot make print request because label list is empty");
        }
    }

    @Test
    public void TestCreatingLabels() throws FileNotFoundException {
        File file = properties.findFile("correct_request.txt");

        Scanner expectedData = new Scanner(file);
        expectedData.nextLine();
        expectedData.nextLine();
        String[] expectedColumns = expectedData.nextLine().split("\\|");

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

        Scanner actualData = new Scanner(file);
        actualData.nextLine();
        actualData.nextLine();
        List<PrintRequest.Label> labels = printRequestHelper.createLabels(actualData);

//       expect the field names to be created from the given file's column headings on the fly
        for (PrintRequest.Label label : labels) {
            assertTrue(label.getFields().keySet().containsAll(columns));
        }
//        expect the number of labels created to be correct
        assertEquals(labels.size(), numOfRequestedLabels);
    }

    @Test
    public void TestMakeRequestFromFile() throws Exception {
        File file = properties.findFile("correct_request.txt");

        PrintRequest request = printRequestHelper.makeRequestFromFile(file, printerList);

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
