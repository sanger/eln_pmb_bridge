package sanger;

import org.apache.log4j.PropertyConfigurator;
import org.codehaus.jettison.json.JSONException;

import java.io.IOException;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException, JSONException {
        System.setProperty("java.net.preferIPv6Addresses", "true");
        PropertyConfigurator.configure("log4j.properties");

//        Console c = System.console();
//        if (c == null) {
//            System.out.println("No console");
//            return;
//        }
//        System.out.println("Enter the name of a file containing a cell line and barcode");
//        String fileName = c.readLine();
//        FileManager manager = new FileManager();
//        PrintRequest request = manager.makeRequestFromFile(fileName);
//
//        PrintConfig.loadConfig();
//        PMBClient pmbClient = new PMBClient(PrintConfig.getInstance());
//        pmbClient.print(request);

        PrintConfig printConfig = PrintConfig.loadConfig();
        PMBClient pmbClient = new PMBClient(printConfig);

        String printerName = "d304bc";

        Map<String, String> fields = new HashMap<>();
        fields.put("cell_line", "nawk");
        fields.put("barcode", "2000000000010");

        List<PrintRequest.Label> labels = new ArrayList<>();
        PrintRequest.Label label = new PrintRequest.Label(fields);
        labels.add(label);

        PrintRequest mockRequest = new PrintRequest(printerName, labels);
        pmbClient.print(mockRequest);
    }
}