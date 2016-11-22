package tests.resource.printResourceTests;

import org.codehaus.jettison.json.*;
import org.testng.annotations.Test;
import sanger.PrintRequest;

import java.io.IOException;
import java.util.*;

/**
 * @author hc6
 */
public class WhenPrintRequestIsSuccessful extends GivenPrintRequest {

    protected PrintRequest request;

    public void When() throws Exception {
        sendRequest(request);
    }

    public void setContext() throws IOException, JSONException {
        String printerName = "d304bc";
        Map<String, String> fieldMap = new HashMap<>();
        fieldMap.put("cell_line", "nawk");
        fieldMap.put("barcode", "2000000100");

        PrintRequest.Label label = new PrintRequest.Label(fieldMap);
        request = new PrintRequest(printerName, Collections.singletonList(label));
    }

    @Test
    public void Then() throws Exception {
        setContext();
        When();

    }


    @Override
    protected JSONObject makeJson() throws JSONException {
        System.out.println("making json");
        System.out.println(request);

        String printer = request.getPrinterName();
        Integer templateId = 6;

        JSONArray body = new JSONArray();
        for (PrintRequest.Label label : request.getLabels()){
            JSONObject labelJson = new JSONObject();
            labelJson.put("label_1", new JSONObject(label.getFields()));
            body.put(labelJson);
        }

        JSONObject labels = new JSONObject();
        labels.put("body", body);

        JSONObject attributes = new JSONObject();
        attributes.put("printer_name", printer);
        attributes.put("label_template_id", templateId);
        attributes.put("labels", labels);

        JSONObject data = new JSONObject();
        data.put("attributes", attributes);

        JSONObject requestJson = new JSONObject();
        requestJson.put("data", data);

        System.out.println("json" + requestJson);
        return requestJson;
    }
}
