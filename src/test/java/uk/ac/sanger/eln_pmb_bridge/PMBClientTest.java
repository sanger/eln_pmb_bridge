package uk.ac.sanger.eln_pmb_bridge;

import org.codehaus.jettison.json.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * @author hc6
 */
public class PMBClientTest {

    @BeforeClass
    public void setUp() throws IOException {
        ELNPMBProperties.setProperties("./test_properties_folder/eln_pmb.properties");
        PrinterConfig.initialise("./test_properties_folder/printer.properties");
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
        verify(client, times(1)).postJsonVoid(any(), any());
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
        verify(client, times(1)).postJsonVoid(any(), any());
    }

    @Test
    public void TestPrintEmptyRequest() throws Exception {
        try {
            PMBClient client = new PMBClient();
            client.print(null);
            fail("An exception should have been thrown.");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Null request in PMBClient.print");
        }
    }

    @Test
    public void whenBuildingJson() throws JSONException, IOException {
        PMBClient pmbClient = new PMBClient();

        Map<String, String> fieldMap1 = new HashMap<>();
        fieldMap1.put("cell_line", "zogh");

        Map<String, String> fieldMap2 = new HashMap<>();
        fieldMap2.put("cell_line", "nawk");

        PrintRequest.Label label1 = new PrintRequest.Label(fieldMap1);
        PrintRequest.Label label2 = new PrintRequest.Label(fieldMap2);

        String printerName = "123456";
        PrintRequest request = new PrintRequest(printerName, Arrays.asList(label1, label2), 3);

        JSONObject result = pmbClient.buildJson(request);
        JSONObject data = result.getJSONObject("data");
        JSONObject attr = data.getJSONObject("attributes");

        assertEquals(attr.getString("printer_name"), printerName);
        assertEquals(attr.getInt("label_template_id"), 1);
        JSONObject labels = attr.getJSONObject("labels");
        JSONArray body = labels.getJSONArray("body");

        assertEquals(body.length(), 6);
        assertEquals(body.getJSONObject(0).getJSONObject("label_1").get("cell_line"), fieldMap1.get("cell_line"));
        assertEquals(body.getJSONObject(1).getJSONObject("label_1").get("cell_line"), fieldMap1.get("cell_line"));
        assertEquals(body.getJSONObject(2).getJSONObject("label_1").get("cell_line"), fieldMap1.get("cell_line"));
        assertEquals(body.getJSONObject(3).getJSONObject("label_1").get("cell_line"), fieldMap2.get("cell_line"));
        assertEquals(body.getJSONObject(4).getJSONObject("label_1").get("cell_line"), fieldMap2.get("cell_line"));
        assertEquals(body.getJSONObject(5).getJSONObject("label_1").get("cell_line"), fieldMap2.get("cell_line"));
    }

    @Test
    public void whenPostingJson(){

    }

}
