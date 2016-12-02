package uk.ac.sanger.eln_pmb_bridge;

import org.codehaus.jettison.json.JSONException;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Properties;

public class Main {

    public static void main(String[] args) throws IOException, JSONException {
        System.setProperty("java.net.preferIPv6Addresses", "true");

        FileManager manager = new FileManager();

        Properties properties = manager.readPropertiesFile("pmb.properties");
        String pollFolder = properties.getProperty("poll_folder", "");
        Path path = Paths.get(pollFolder);
        String filename = "";

        try {
            WatchService watcher = path.getFileSystem().newWatchService();
            path.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
            WatchKey watchKey = watcher.take();
            List<WatchEvent<?>> watchEvents = watchKey.pollEvents();
            for (WatchEvent event : watchEvents) {
                filename = event.context().toString();
                System.out.println("Created: " + event.context().toString());
            }
        } catch (Exception e) {
            System.out.println("error" + e.toString());
        }

        PrintRequest request = manager.makeRequestFromFile(filename);

        PrintConfig printConfig = PrintConfig.loadConfig();
        PMBClient pmbClient = new PMBClient(printConfig);

        pmbClient.print(request);

    }
}