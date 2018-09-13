package uk.ac.sanger.eln_pmb_bridge;

import org.testng.annotations.Test;

import java.io.IOException;
import java.util.*;

import static org.testng.Assert.assertEquals;

/**
 * @author hc6
 */
public class PrintRequestTest {

    @Test
    public void TestPrintRequest() throws IOException {

        String printerName = "123456";
        String cellLine = "zogh";
        int numOfCopies = 3;
        String passage_number = "123";

        Map<String, String> fieldMap = new HashMap<>();
        fieldMap.put("cell_line", cellLine);
        fieldMap.put("passage_number", passage_number);

        PrintRequest.Label label = new PrintRequest.Label(fieldMap);
        List<PrintRequest.Label> labels = Collections.singletonList(label);

        PrintRequest request = new PrintRequest(printerName, labels, numOfCopies);

        assertEquals(request.getLabels(), labels);
        assertEquals(request.getPrinterName(), printerName);
        assertEquals(request.getNumOfCopies(), numOfCopies);
        assertEquals(request.length(), labels.size());

        PrintRequest.Label requestLabel = request.getLabels().get(0);
        assertEquals(requestLabel.getFields(), fieldMap);
        assertEquals(requestLabel.getField("cell_line"), cellLine);
        assertEquals(requestLabel.getField("passage_number"), passage_number);
    }
}
