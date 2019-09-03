package uk.ac.sanger.eln_pmb_bridge;

import org.codehaus.jettison.json.*;
import org.testng.annotations.*;
import uk.ac.sanger.eln_pmb_bridge.PrintRequest.Label;
import uk.ac.sanger.eln_pmb_bridge.PrinterConfig.Entry;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * @author dr6
 */
public class SPrintClientTest {
    private SPrintConfig sprintConfig;
    private StringTemplate template;
    private PrinterConfig printerConfig;
    private SPrintClient client;
    private PrintRequest request;
    private String printerName = "printer1";

    @BeforeClass
    private void setup() throws IOException, JSONException {
        template = new StringTemplate("{\"barcode\":\"#barcode#\"}", "#", "#");
        Map<LabelType, StringTemplate> templates = new EnumMap<>(LabelType.class);
        templates.put(LabelType.thin, template);
        sprintConfig = new SPrintConfig("http://sprint/graphql", templates);
        printerConfig = new PrinterConfig(Collections.singletonMap(printerName,
                new Entry(PrinterConfig.Service.SPRINT, LabelType.thin)));
        List<Label> labels = Arrays.asList(
                new Label(map("barcode", "CGAP-1")),
                new Label(map("barcode", "CGAP-2"))
        );
        request = new PrintRequest(printerName, labels, 3);
    }

    @BeforeMethod
    private void testSetup() {
        client = spy(new SPrintClient(printerConfig, sprintConfig));
    }

    @Test
    public void testSuccessfulPrint() throws Exception {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("something", "something");
        doReturn(jsonRequest).when(client).toJson(any(), any());

        doReturn(new JSONObject()).when(client).postJson(any(), any());

        client.print(request);

        verify(client).toJson(request, template);
        verify(client).postJson(new URL(sprintConfig.getHost()), jsonRequest);
    }

    @Test
    public void testPrintError() throws Exception {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("something", "something");
        doReturn(jsonRequest).when(client).toJson(any(), any());

        JSONObject error = new JSONObject();
        error.put("message", "Didn't work");
        JSONObject response = new JSONObject();
        response.put("errors", Collections.singletonList(error));
        doReturn(response).when(client).postJson(any(), any());

        try {
            client.print(request);
            fail("Expected an exception");
        } catch (IOException e) {
            assertTrue(e.getMessage().contains("Didn't work"));
        }

        verify(client).toJson(request, template);
        verify(client).postJson(new URL(sprintConfig.getHost()), jsonRequest);
    }

    @Test
    public void testToJson() throws JSONException {
        JSONObject result = client.toJson(request, template);
        assertTrue(result.has("query"));

        JSONObject variables = result.getJSONObject("variables");
        assertTrue(variables.has("printer"));
        assertTrue(variables.has("printRequest"));
        assertEquals(variables.length(), 2);

        assertEquals(variables.getString("printer"), printerName);
        JSONObject requestJson = variables.getJSONObject("printRequest");
        assertEquals(requestJson.length(), 1);
        assertTrue(requestJson.has("layouts"));

        int num = request.getLabels().size() * request.getNumOfCopies();
        List<String> expectedBarcodes = new ArrayList<>(num);
        for (Label label : request.getLabels()) {
            for (int i = 0; i < request.getNumOfCopies(); ++i) {
                expectedBarcodes.add(label.getField("barcode"));
            }
        }
        JSONArray labelArray = requestJson.getJSONArray("layouts");
        assertEquals(labelArray.length(), num);
        for (int i = 0; i < num; ++i) {
            JSONObject label = labelArray.getJSONObject(i);
            assertEquals(label.length(), 1);
            assertTrue(label.has("barcode"));
            assertEquals(label.getString("barcode"), expectedBarcodes.get(i));
        }
    }

    private static Map<String, String> map(String... elements) {
        if (elements.length==0) {
            return Collections.emptyMap();
        }
        if (elements.length==2) {
            return Collections.singletonMap(elements[0], elements[1]);
        }
        Map<String, String> map = new HashMap<>(elements.length/2);
        for (int i = 0; i < elements.length; i += 2) {
            map.put(elements[i], elements[i+1]);
        }
        return map;
    }
}
