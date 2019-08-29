package uk.ac.sanger.eln_pmb_bridge;

import org.codehaus.jettison.json.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.sanger.eln_pmb_bridge.PrintRequest.Label;

import java.io.IOException;
import java.net.URL;

/**
 * Client for the new print service
 * @author dr6
 */
public class SPrintClient extends BaseClient implements PrintService {
    private static final Logger log = LoggerFactory.getLogger(SPrintClient.class);

    private PrinterConfig printerConfig;
    private SPrintConfig sprintConfig;

    public SPrintClient(PrinterConfig printerConfig, SPrintConfig sprintConfig) {
        this.printerConfig = printerConfig;
        this.sprintConfig = sprintConfig;
    }

    public SPrintClient() {
        this(PrinterConfig.getInstance(), SPrintConfig.getInstance());
    }

    @Override
    public void print(PrintRequest request) throws Exception {
        LabelType labelType = printerConfig.getPrinterConfig(request.getPrinterName()).labelType;
        StringTemplate template = sprintConfig.getTemplate(labelType);
        JSONObject printRequest = toJson(request, template);
        JSONObject response = postJson(new URL(sprintConfig.getHost()), printRequest);
        checkResponse(response);
    }

    /**
     * Checks the response from the print service for indication of errors.
     * For SPrint, if there are errors they will be in an array under the key "errors".
     * If an error is found, an {@code IOException} will be thrown including the message from the first
     * error in the array.
     * @param response the parsed JSON from the print service's response
     * @exception JSONException the response could not be parsed
     * @exception IOException if the response indicated that there were errors
     */
    public void checkResponse(JSONObject response) throws JSONException, IOException {
        if (response.has("errors")) {
            log.error("Error from SPrint:");
            log.error(response.toString(4));
            throw new IOException(getSprintErrorMessage(response));
        }
        log.info("Request accepted by SPrint");
    }

    /**
     * Converts the print request to a JSON object for transmission to the print service.
     * @param request the specification of what to print
     * @param template the sprint template
     * @return a JSON object representing the data that will be sent to the print service.
     * @exception JSONException the conversion could not be performed
     */
    public JSONObject toJson(PrintRequest request, StringTemplate template) throws JSONException {
        String printerName = request.getPrinterName();
        JSONObject variables = new JSONObject();
        variables.put("printer", printerName);

        JSONArray layouts = new JSONArray();

        int numCopies = request.getNumOfCopies();
        for (Label label : request.getLabels()) {
            String layoutString = template.substitute(label.getFields());
            JSONObject layoutObject = new JSONObject(layoutString);
            for (int i = 0; i < numCopies; ++i) {
                layouts.put(layoutObject);
            }
        }

        JSONObject printRequest = new JSONObject();
        printRequest.put("layouts", layouts);
        variables.put("printRequest", printRequest);

        JSONObject printMutation = new JSONObject();
        printMutation.put("query", getQueryText());
        printMutation.put("variables", variables);

        return printMutation;
    }

    private String getQueryText() {
        return "mutation ($printer:String!, $printRequest:PrintRequest!) {" +
                "  print(printer: $printer, printRequest: $printRequest) {" +
                "    jobId" +
                "  }" +
                "}";
    }

    private String getSprintErrorMessage(JSONObject response) throws JSONException {
        return response.getJSONArray("errors").getJSONObject(0).getString("message");
    }
}
