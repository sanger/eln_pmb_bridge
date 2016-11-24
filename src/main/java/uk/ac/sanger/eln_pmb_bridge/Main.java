package uk.ac.sanger.eln_pmb_bridge;

import org.codehaus.jettison.json.JSONException;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, JSONException {
        System.setProperty("java.net.preferIPv6Addresses", "true");

        if (args.length!=1) {
            System.err.println("The arguments to this program should be a filename.");
            System.exit(1);
        }
        String filename = args[0];

        FileManager manager = new FileManager();
        PrintRequest request = manager.makeRequestFromFile(filename);

        PrintConfig printConfig = PrintConfig.loadConfig();
        PMBClient pmbClient = new PMBClient(printConfig);

        pmbClient.print(request);

    }
}