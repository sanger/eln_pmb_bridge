package uk.ac.sanger.eln_pmb_bridge;

import org.codehaus.jackson.map.ObjectMapper;
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

        final String printerName = "123456";
        final String cellLine = "zogh";
        final String barcode = "290re018d96";

        Map<String, String> fieldMap = new HashMap<>();
        fieldMap.put("cell_line", cellLine);
        fieldMap.put("barcode", barcode);

        PrintRequest.Label label = new PrintRequest.Label(fieldMap);
        PrintRequest request = new PrintRequest(printerName, Collections.singletonList(label));

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(request);
        PrintRequest result = objectMapper.readValue(jsonString, PrintRequest.class);

        assertEquals(result, request);
        assertEquals(result.length(), 1);
        assertEquals(result.getPrinterName(), printerName);

        PrintRequest.Label label1 = result.getLabels().get(0);
        assertEquals(label1.getField("cell_line"), cellLine);
        assertEquals(label1.getField("barcode"), barcode);
    }
}
