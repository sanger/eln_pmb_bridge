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
        PrintConfig.loadConfig();
        pmbClient = new PMBClient(PrintConfig.getInstance());

        Map<PrinterLabelType, Integer> templateIds = new EnumMap<>(PrinterLabelType.class);
        templateIds.put(PrinterLabelType.Plate, 6);
        templateIds.put(PrinterLabelType.Tube, 0);
        templateIds.put(PrinterLabelType.Branded, 0);
        pmbClient = new PMBClient(new PrintConfig("", templateIds));

        Map<String, String> fieldMap = new HashMap<>();
        fieldMap.put("cell_line", "zogh");
        fieldMap.put("barcode", "2000000100");

        PrintRequest.Label label = new PrintRequest.Label(fieldMap);
        String printerName = "d304bc";
        request = new PrintRequest(printerName, Collections.singletonList(label));

        result = pmbClient.buildJson(request);
        assertEquals(result.length(), 1);

        JSONObject data = result.getJSONObject("data");

        JSONObject attr = data.getJSONObject("attributes");
        assertEquals(attr.length(), 3);
        assertEquals(attr.getString("printer_name"), printerName);
        assertEquals(attr.getInt("label_template_id"), (int) templateIds.get(PrinterLabelType.Plate));

        JSONObject labels = attr.getJSONObject("labels");
        assertEquals(labels.length(), 1);
        JSONArray body = labels.getJSONArray("body");

        int i = 0;
        for (PrintRequest.Label l : request.getLabels()) {
            JSONObject item = body.getJSONObject(i);
            assertEquals(item.length(), 1);
            JSONObject itemLabel = item.getJSONObject("label_1");
            assertEquals(itemLabel.length(), l.getFields().size());
            for (Map.Entry<String, String> entry : l.getFields().entrySet()) {
                assertEquals(itemLabel.get(entry.getKey()), entry.getValue());
            }
            ++i;
        }
    }

}
