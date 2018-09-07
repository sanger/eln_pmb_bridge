package uk.ac.sanger.eln_pmb_bridge;

import org.codehaus.jettison.json.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

/**
 * @author hc6
 */
public class PMBClientTest {

    @BeforeClass
    public void setUp() throws IOException {
        ELNPMBProperties.setProperties("./test_properties_folder/eln_pmb.properties");
        PrinterProperties.setProperties("./test_properties_folder/printer.properties");
    }

    @Test
    public void TestPrintSuccessful() throws Exception {
        PrintRequestHelper printRequestHelper = new PrintRequestHelper();
        String correctPollFile = "./test_examples/correct_request_test.txt";
        PrintRequest request = printRequestHelper.makeRequestFromFile(Paths.get(correctPollFile));

        PMBClient client = mock(PMBClient.class);
        doCallRealMethod().when(client).print(request);
        client.print(request);

        verify(client, times(1)).buildJson(request);
        verify(client, times(1)).postJson(any(), any());
    }

    @Test
    public void TestPrintSuccessfulWithMultipleNumOfCopies() throws Exception {
        PrintRequestHelper printRequestHelper = new PrintRequestHelper();
        String correctPollFile = "./test_examples/correct_request_test_multiple_copies.txt";
        PrintRequest request = printRequestHelper.makeRequestFromFile(Paths.get(correctPollFile));

        PMBClient client = mock(PMBClient.class);
        doCallRealMethod().when(client).print(request);
        client.print(request);

        verify(client, times(1)).buildJson(request);
        verify(client, times(3)).postJson(any(), any());
    }

    @Test
    public void TestPrintEmptyRequest() throws Exception {
        try {
            PMBClient client = new PMBClient();
            client.print(null);
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Null request in PMBClient.print");
        }
    }

    @Test
    public void whenBuildingJson() throws JSONException, IOException {

        Map<String, Integer> templateIds = PrinterProperties.getPrinterTemplateIdList();
        PMBClient pmbClient = new PMBClient();

        Map<String, String> fieldMap = new HashMap<>();
        fieldMap.put("cell_line", "zogh");
        fieldMap.put("barcode", "2000000100");

        PrintRequest.Label label1 = new PrintRequest.Label(fieldMap);

        String printerName = "123456";
        PrintRequest request = new PrintRequest(printerName, Collections.singletonList(label1), 1);

        JSONObject result = pmbClient.buildJson(request);
        assertEquals(result.length(), 1);

        JSONObject data = result.getJSONObject("data");
        JSONObject attr = data.getJSONObject("attributes");
        assertEquals(attr.length(), 3);
        assertEquals(attr.getString("printer_name"), printerName);
        assertEquals(attr.getInt("label_template_id"), (int) templateIds.get("123456"));

        JSONObject labels = attr.getJSONObject("labels");
        assertEquals(labels.length(), 1);
        JSONArray body = labels.getJSONArray("body");

        int i = 0;
        for (PrintRequest.Label label : request.getLabels()) {
            JSONObject labelJson = body.getJSONObject(i);
            assertEquals(labelJson.length(), 1);
            JSONObject fieldsMap = labelJson.getJSONObject("label_1");
            assertEquals(fieldsMap.length(), label.getFields().size());
            for (Map.Entry<String, String> entry : label.getFields().entrySet()) {
                assertEquals(fieldsMap.get(entry.getKey()), entry.getValue());
            }
            ++i;
        }
    }

    @Test
    public void whenPostingJson(){

    }

}
