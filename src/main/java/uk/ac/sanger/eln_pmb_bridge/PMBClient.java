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
 * A printer client specifically for PrintMyBarcode
 * @author hc6
 */
public class PMBClient implements PrintService {
    private static final Logger log = LoggerFactory.getLogger(PMBClient.class);

    @Override
    public void print(PrintRequest request) throws Exception {
        if (request==null){
            throw new IllegalArgumentException("Null request in PMBClient.print");
        }

        URL url = new URL(ELNPMBProperties.getPMBURL());
        JSONObject jsonObject = buildJson(request);

        postJson(url, jsonObject);
        logPrintSuccessful(request);
    }

    /**
     * Builds a JSON object from the new print job request
     */
    protected JSONObject buildJson(PrintRequest request) throws JSONException {
        JSONArray JSONBody = createJSONBody(request);
        return constructJSONRequest(request, JSONBody);
    }

    /**
     * Builds the JSON object for the labels
     */
    private JSONArray createJSONBody(PrintRequest request) throws JSONException {
        JSONArray body = new JSONArray();

        int labelIndex = 1;
        for (PrintRequest.Label label : request.getLabels()){
            JSONObject labelJson = new JSONObject();
            String labelNumber = String.format("label_%s", labelIndex++);
            labelJson.put(labelNumber, new JSONObject(label.getFields()));

            for (int i = 0; i < request.getNumOfCopies(); i++) {
                body.put(labelJson);
            }
        }
        return body;
    }

    /**
     * Builds the rest of the JSON request
     */
    private JSONObject constructJSONRequest(PrintRequest request, JSONArray JSONBody) throws JSONException {
        JSONObject labels = new JSONObject();
        labels.put("body", JSONBody);

        String printer = request.getPrinterName();
        Integer templateId = PrinterProperties.getTemplateIdForPrinter(printer);

        JSONObject attributes = new JSONObject();
        attributes.put("printer_name", printer);
        attributes.put("label_template_id", templateId);
        attributes.put("labels", labels);

        JSONObject data = new JSONObject();
        data.put("attributes", attributes);

        JSONObject JSONRequest = new JSONObject();
        return JSONRequest.put("data", data);
    }

    /**
     * Opens a connection and sets the request headers
     * Posts JSON object to PrintMyBarcode
     * @param targetURL the url to send the request to
     * @param jsonObject JSON to post
     */
    protected void postJson(URL targetURL, Object jsonObject) throws IOException {
        HttpURLConnection connection = null;
        int responseCode = 0;
        try {
            connection = (HttpURLConnection) targetURL.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write(jsonObject.toString());
            out.flush();
        } finally {
            if (connection != null) {
                responseCode = connection.getResponseCode();
                connection.disconnect();
            }
        }

        if (responseCode!=HTTP_CREATED) {
            log.error("HTTP Response code: " + responseCode);
            throw new HTTPException(responseCode);
        } else {
            log.info("HTTP Response code: " + responseCode);
        }
    }

    private void logPrintSuccessful(PrintRequest request) {
        String printer = request.getPrinterName();
        for (PrintRequest.Label label : request.getLabels()) {
            log.info(String.format("Printed cell line %s at printer %s", label.getField("cell_line"), printer));
        }
    }
}
