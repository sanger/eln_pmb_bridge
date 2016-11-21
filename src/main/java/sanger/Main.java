package sanger;
;
import org.codehaus.jettison.json.JSONException;

import java.io.IOException;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException, JSONException {
        System.setProperty("java.net.preferIPv6Addresses", "true");

//        Console c = System.console();
//        if (c == null) {
//            System.out.println("No console");
//            return;
//        }
//        System.out.println("Enter the name of a file containing a cell line and barcode");
//        String fileName = c.readLine();
//        FileManager manager = new FileManager();
//        PrintRequest request = manager.readFile(fileName);

//        String response = pmb.getJson(localUrl);
//        System.out.println("Response: "+ response);

        PrintConfig.loadConfig();

        PMBClient pmbClient = new PMBClient(PrintConfig.getInstance());
        String printerName = "d304bc";

        Map<String, String> fields = new HashMap<>();
        fields.put("cell_line", "zogh");
        fields.put("barcode", "200000000111");

//        eg [Label{fields={cell_line=zogh, barcode=200000000111}}, Label{fields={cell_line=zogh, barcode=200000000111}}]
        List<PrintRequest.Label> labels = new ArrayList<>();
        PrintRequest.Label label = new PrintRequest.Label(fields);
        labels.add(label);

        PrintRequest mockRequest = new PrintRequest(printerName, labels);
        pmbClient.print(mockRequest);

    }
}