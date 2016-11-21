package sanger;

import org.codehaus.jettison.json.*;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * @author hc6
 */
public class PMBClient {
    private final PrintConfig config;

    public PMBClient(PrintConfig config) {
        this.config = config;
    }

    public void print(PrintRequest request) throws IOException, JSONException {
        JSONObject jsonObject = buildJson(request);
        System.out.println(jsonObject);

        URL url = new URL(config.getLocalLocation()+"/print_jobs");
        postJson(url, jsonObject);
    }

    private JSONObject buildJson(PrintRequest request) throws JSONException {
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

    protected void postJson(URL targetURL, Object jsonObject) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) targetURL.openConnection();

        try {
            System.out.println(connection);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            setHeaders(connection);

            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write(jsonObject.toString());
            out.flush();

            int responseCode = connection.getResponseCode();
            System.out.println("Response code: " + responseCode);

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
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
    }

//    protected String getJson(String targetURL) throws IOException {
//        URL url = new URL(targetURL);
//        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//
//        try {
//            System.out.println(connection);
//
//            connection.setRequestMethod("GET");
//            setHeaders(connection);
//            int responseCode = connection.getResponseCode();
//            System.out.println("Response code: "+responseCode);
//
//            if (responseCode==HTTP_NOT_FOUND) {
//                throw new IOException(HTTP_NOT_FOUND+" - NOT FOUND");
//            }
//            if (responseCode!=HTTP_OK) {
//                throw new IOException(getResponseString(connection.getErrorStream()));
//            }
//            return getResponseString(connection.getInputStream());
//
//        } finally {
//            if (connection != null) {
//                connection.disconnect();
//            }
//        }
//    }
//
//    private String getResponseString(InputStream input) throws IOException {
//        if (input==null) {
//            return null;
//        }
//
//        BufferedReader rd = new BufferedReader(new InputStreamReader(input));
//        StringBuilder response = new StringBuilder();
//        String line;
//        while ((line = rd.readLine()) != null) {
//            response.append(line);
//            response.append('\r');
//        }
//        rd.close();
//        return response.toString();
//    }

}
