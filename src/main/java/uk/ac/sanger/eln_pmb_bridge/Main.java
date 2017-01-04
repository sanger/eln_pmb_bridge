package uk.ac.sanger.eln_pmb_bridge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(PrintConfig.class);
    private static final String CURRENT_VERSION = "1.0";

    public static void main(String[] args) throws IOException {
        /**
         * When a host has both IPv4 and IPv6 addresses, change preference to use IPv6 addresses over IPv4
         */
        System.setProperty("java.net.preferIPv6Addresses", "true");
        log.info("CURRENT_VERSION: " + CURRENT_VERSION);

        FileManager manager = new FileManager();
        manager.setPMBProperties();

        Path pollPath = manager.getPollFolderPath();
        /**
         * Set up email service to send an email when the service starts and when there is an unrecoverable error
         */
        EmailService emailManager = new EmailService(manager.getMailProperties());
        /**
         * WatchService monitors the poll folder specified in pmb.properties
         * A watchable object is registered with a particular type of event
         * When that event is detected, a key is added to the watch service que
         * The take method returns the watch key from the service when it becomes available
         * The event is processed and the newly created file name returned
         */
        String newFileName = "";
        try {
            String currentTime = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
            String startUpMessage = String.format("Starting up ELN PMB Bridge service at %s, version number: %s",
                    currentTime, CURRENT_VERSION);
            emailManager.sendEmail("Starting up ELN PMB Service", startUpMessage);

            WatchService watcher = pollPath.getFileSystem().newWatchService();
            pollPath.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
            WatchKey watchKey;

            while ((watchKey = watcher.take()) !=null) {
                List<WatchEvent<?>> watchEvents = watchKey.pollEvents();

                for (WatchEvent event : watchEvents) {
                    newFileName = event.context().toString();
                    log.info(String.format("New file \"%s\" in polled folder", event.context().toString()));
                    PrintRequest request = manager.makeRequestFromFile(newFileName);
                    Properties properties = manager.getPMBProperties();
                    PrintConfig printConfig = PrintConfig.loadConfig(properties);
                    PMBClient pmbClient = new PMBClient(printConfig);
                    pmbClient.print(request);
                    manager.archiveFile(newFileName);
                }
                watchKey.reset();
            }
        } catch (Exception e) {
            log.error("Watch service error");
            try {
                String errorMessage = String.format("Error when trying to print labels via PrintMyBarcode from an polled ELN file. %s" +
                        "Moving file to error folder", e);
                emailManager.sendEmail("ELN PMB Bridge - error", errorMessage);
                manager.errorFile(newFileName);
            } catch (Exception e1) {
                log.debug("Problem sending error email");
            }
            log.error("Watch service error");
        }

    }
}