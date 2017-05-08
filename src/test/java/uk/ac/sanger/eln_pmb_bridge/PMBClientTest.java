package uk.ac.sanger.eln_pmb_bridge;

import org.codehaus.jettison.json.*;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.*;

import static org.testng.Assert.assertEquals;

/**
 * @author hc6
 */
public class PMBClientTest {

    @Test
    public void TestGetPrintConfigSuccessful(){

    }

    @Test
    public void TestGetPrintConfigNotSuccessful(){

    }

    @Test
    public void whenGivenRequestIsNull(){

    }

    @Test
    public void whenBuildingJson() throws JSONException, IOException {

        Map<String, Integer> templateIds = new HashMap<>();
        templateIds.put("d304bc", 6);
        templateIds.put("e367bc", 0);
        PMBClient pmbClient = new PMBClient(new PrintConfig("", templateIds));

        Map<String, String> fieldMap = new HashMap<>();
        fieldMap.put("cell_line", "zogh");
        fieldMap.put("barcode", "2000000100");

        PrintRequest.Label label1 = new PrintRequest.Label(fieldMap);
        String printerName = "e367bc";
        PrintRequest request = new PrintRequest(printerName, Collections.singletonList(label1));

        JSONObject result = pmbClient.buildJson(request);
        assertEquals(result.length(), 1);

        JSONObject data = result.getJSONObject("data");
        JSONObject attr = data.getJSONObject("attributes");
        assertEquals(attr.length(), 3);
        assertEquals(attr.getString("printer_name"), printerName);
        assertEquals(attr.getInt("label_template_id"), (int) templateIds.get("e367bc"));

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
