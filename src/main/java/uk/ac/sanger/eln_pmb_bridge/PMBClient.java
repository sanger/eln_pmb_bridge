package uk.ac.sanger.eln_pmb_bridge;

import org.codehaus.jettison.json.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.*;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;

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
     * Sets the request headers and post a json request to pmb
     * @param request the request to print
     */
    public void print(PrintRequest request) throws JSONException, IOException {
        if (request==null){
            return;
        }
        JSONObject jsonObject = buildJson(request);
        URL url = new URL(config.getPmbURL());
        postJson(url, jsonObject);
        for (PrintRequest.Label label : request.getLabels()) {
            String logString = String.format("Printed barcode %s at printer %s",
                    label.getField("barcode"),request.getPrinterName());
            log.info(logString);
        }
    }

    public JSONObject buildJson(PrintRequest request) throws JSONException {
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

        JSONObject requestJson = new JSONObject();
        requestJson.put("data", data);

        return requestJson;
    }

    protected void postJson(URL targetURL, Object jsonObject) throws IOException, JSONException {
        HttpURLConnection connection = (HttpURLConnection) targetURL.openConnection();
        try {
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            setHeaders(connection);

            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write(jsonObject.toString());
            out.flush();

            int responseCode = connection.getResponseCode();
            log.debug("Response code: {}", responseCode);
            if (responseCode == HTTP_NOT_FOUND) {
                throw new IOException(HTTP_NOT_FOUND + " - NOT FOUND");
            }
            if (responseCode != HTTP_OK) {
                throw new IOException();
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void setHeaders(HttpURLConnection connection) {
        connection.setRequestProperty("Content-Type", "application/json");
    }

}
