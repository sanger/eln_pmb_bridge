package tests.parameters;


import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.Test;
import sanger.PrintRequest;

import java.io.IOException;
import java.util.*;

import static org.testng.Assert.assertEquals;

/**
 * @author hc6
 */
@Test
public class PrintRequestTest {
    public void TestPrintLabwareRequest() throws IOException {

        final String printerName = "d304bc";
        final String cellLine = "zogh";
        final String barcode = "200";

        Map<String, String> fieldMap = new HashMap<>();
        fieldMap.put("cell_line", cellLine);
        fieldMap.put("barcode", barcode);

//        eg [Label{fields={cell_line=zogh, barcode=200000000111}}, Label{fields={cell_line=zogh, barcode=200000000111}}]
        PrintRequest.Label label = new PrintRequest.Label(fieldMap);
        PrintRequest request = new PrintRequest(printerName, Collections.singletonList(label));

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(request);
        PrintRequest result = objectMapper.readValue(jsonString, PrintRequest.class);

        assertEquals(result.getLabels(), request.getLabels());
        assertEquals(label.getField("cell_line"), cellLine);
        assertEquals(label.getField("barcode"), barcode);

    }
}
