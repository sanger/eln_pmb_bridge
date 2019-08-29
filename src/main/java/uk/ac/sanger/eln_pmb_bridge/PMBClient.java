package uk.ac.sanger.eln_pmb_bridge;

import org.codehaus.jettison.json.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.sanger.eln_pmb_bridge.PrinterConfig.Entry;

import java.net.URL;

/**
 * A printer client specifically for PrintMyBarcode
 * @author hc6
 */
public class PMBClient extends BaseClient implements PrintService {
    private static final Logger log = LoggerFactory.getLogger(PMBClient.class);

    private PrinterConfig printerConfig;

    public PMBClient(PrinterConfig printerConfig) {
        this.printerConfig = printerConfig;
    }

    public PMBClient() {
        this(PrinterConfig.getInstance());
    }

    @Override
    public void print(PrintRequest request) throws Exception {
        if (request==null){
            throw new IllegalArgumentException("Null request in PMBClient.print");
        }

        URL url = new URL(ELNPMBProperties.getPMBURL());
        JSONObject jsonObject = buildJson(request);

        postJsonVoid(url, jsonObject);
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
     * PMB only accepts label data object with key "label_1"
     */
    private JSONArray createJSONBody(PrintRequest request) throws JSONException {
        JSONArray body = new JSONArray();

        for (PrintRequest.Label label : request.getLabels()){
            JSONObject labelJson = new JSONObject();
            labelJson.put("label_1", new JSONObject(label.getFields()));

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
        Entry entry = printerConfig.getPrinterConfig(printer);
        Integer templateId = ELNPMBProperties.getPMBTemplateId(entry.labelType);

        JSONObject attributes = new JSONObject();
        attributes.put("printer_name", printer);
        attributes.put("label_template_id", templateId);
        attributes.put("labels", labels);

        JSONObject data = new JSONObject();
        data.put("attributes", attributes);

        JSONObject JSONRequest = new JSONObject();
        return JSONRequest.put("data", data);
    }

    private void logPrintSuccessful(PrintRequest request) {
        String printer = request.getPrinterName();
        for (PrintRequest.Label label : request.getLabels()) {
            log.info(String.format("Printed cell line %s at printer %s", label.getField("cell_line"), printer));
        }
    }
}
