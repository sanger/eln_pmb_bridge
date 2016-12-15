package uk.ac.sanger.eln_pmb_bridge;

import org.codehaus.jettison.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Properties;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(PrintConfig.class);

    public static void main(String[] args) throws IOException, JSONException {
        System.setProperty("java.net.preferIPv6Addresses", "true");

        FileManager manager = new FileManager();
        manager.setPMBProperties();

        Path pollPath = manager.getPollFolderPath();
        String newFile = "";

        /**
         * WatchService monitors the poll folder specified in pmb.properties
         * A watchable object is registered with a particular type of event
         * When that event is detected, a key is added to the watch service que
         * The take method returns the watch key from the service when it becomes available
         * The event is processed and the newly created file name returned
         */
        try {
            WatchService watcher = pollPath.getFileSystem().newWatchService();
            pollPath.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
            WatchKey watchKey = watcher.take();
            List<WatchEvent<?>> watchEvents = watchKey.pollEvents();
            for (WatchEvent event : watchEvents) {
                newFile = event.context().toString();
                log.info(String.format("New file %s in polled folder", event.context().toString()));
            }
        } catch (Exception e) {
            log.error("Watch service error", e);
        }

        PrintRequest request = manager.makeRequestFromFile(newFile);

        Properties properties = manager.getPMBProperties();
        PrintConfig printConfig = PrintConfig.loadConfig(properties);
        PMBClient pmbClient = new PMBClient(printConfig);

        pmbClient.print(request);
        manager.archiveFile(newFile);
    }
}