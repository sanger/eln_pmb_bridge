package sanger.service;

import org.codehaus.jettison.json.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sanger.parameters.PrintRequest;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.*;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * @author hc6
 */
public class PMBClient {
    private static final Logger log = LoggerFactory.getLogger(PMBClient.class);
    private final PrintConfig config;

    public PMBClient(PrintConfig config) {
        this.config = config;
    }

    public void print(PrintRequest request) throws JSONException, IOException {
        JSONObject jsonObject = buildJson(request);
        URL url = new URL(config.getPmbURL()+"/print_jobs");
        try {
            postJson(url, jsonObject);
            for (PrintRequest.Label label : request.getLabels()) {
                String logString = String.format("User %s printed barcode %s at printer %s",
                        System.getProperty("user.name"), label.getField("barcode"),request.getPrinterName());
                log.info(logString);
            }
        } catch (IOException e) {
            log.error("Failed to post json to {}", url);
            e.printStackTrace();
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
            log.info("Response code: {}", responseCode);

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
        connection.setRequestProperty("Accept", "application/json");
        log.info("Setting headers for connection {}", connection);
    }

}
