package uk.ac.sanger.eln_pmb_bridge;

import org.codehaus.jettison.json.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.ws.http.HTTPException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.net.HttpURLConnection.HTTP_CREATED;

/**
 * Client for sending print job requests to PrintMyBarcode
 * @author hc6
 */
public class PMBClient {
    private static final Logger log = LoggerFactory.getLogger(PMBClient.class);
    private final PrintConfig config;

    public PMBClient(PrintConfig config) {
        this.config = config;
    }
    /**
     * Builds a json object from the request
     * Sets the request headers
     * Posts a json request to pmb
     * @param request the request to print
     */
    public void print(PrintRequest request) throws Exception {
        if (request==null){
            throw new Exception("Print request error - check log");
        }
        URL url = new URL(config.getPmbURL());
        JSONObject jsonObject = buildJson(request);
        postJson(url, jsonObject);
        for (PrintRequest.Label label : request.getLabels()) {
            log.info(String.format("Printed barcode %s at printer %s",
                    label.getField("barcode"), request.getPrinterName()));
        }
    }

    public JSONObject buildJson(PrintRequest request) {
        JSONObject requestJson = new JSONObject();
        try {
            String printer = request.getPrinterName();
            Integer templateId = config.getTemplateIdForPrinter(printer);

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

            requestJson.put("data", data);

        } catch (JSONException e) {
            log.debug("Failed to build JSON object");
        }
        return requestJson;
    }

    protected void postJson(URL targetURL, Object jsonObject) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) targetURL.openConnection();

        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        setHeaders(connection);

        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
        out.write(jsonObject.toString());
        out.flush();

        connection.disconnect();
        int responseCode = connection.getResponseCode();
        if (responseCode!=HTTP_CREATED){
            throw new HTTPException(responseCode);
        }
        log.debug("HTTP Response code: " + responseCode);
    }

    private void setHeaders(HttpURLConnection connection) {
        connection.setRequestProperty("Content-Type", "application/json");
    }

}
