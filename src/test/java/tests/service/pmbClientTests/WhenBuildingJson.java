package tests.service.pmbClientTests;

import org.codehaus.jettison.json.*;
import org.testng.annotations.Test;
import sanger.*;

import java.io.IOException;
import java.util.*;

import static org.testng.Assert.assertEquals;

/**
 * @author hc6
 */
public class WhenBuildingJson {
    protected JSONObject result;
    protected PMBClient pmbClient;
    protected PrintRequest request;

    @Test
    public void setContext() throws JSONException, IOException {

        Map<PrinterLabelType, Integer> templateIds = new EnumMap<>(PrinterLabelType.class);
        templateIds.put(PrinterLabelType.Plate, 6);
        templateIds.put(PrinterLabelType.Tube, 0);
        templateIds.put(PrinterLabelType.Branded, 0);
        pmbClient = new PMBClient(new PrintConfig("", templateIds));

        Map<String, String> fieldMap = new HashMap<>();
        fieldMap.put("cell_line", "zogh");
        fieldMap.put("barcode", "2000000100");

        PrintRequest.Label label1 = new PrintRequest.Label(fieldMap);
        String printerName = "e367bc";
        request = new PrintRequest(printerName, Collections.singletonList(label1));

        result = pmbClient.buildJson(request);
        assertEquals(result.length(), 1);

        JSONObject data = result.getJSONObject("data");
        JSONObject attr = data.getJSONObject("attributes");
        assertEquals(attr.length(), 3);
        assertEquals(attr.getString("printer_name"), printerName);
        assertEquals(attr.getInt("label_template_id"), (int) templateIds.get(PrinterLabelType.Tube));

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

}
