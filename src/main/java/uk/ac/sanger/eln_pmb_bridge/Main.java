package uk.ac.sanger.eln_pmb_bridge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;
import java.io.IOException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(PrintConfig.class);
    private static final String CURRENT_VERSION = "1.0";
    private static final FileManager manager = new FileManager();

    public static void main(String[] args) throws Exception {
        /**
         * When a host has both IPv4 and IPv6 addresses, change preference to use IPv6 addresses over IPv4
         */
        System.setProperty("java.net.preferIPv6Addresses", "true");
        log.info("CURRENT_VERSION: " + CURRENT_VERSION);

        try {
            manager.setPMBProperties();
            sendStartUpMessage();
            startService();
        } catch (Exception e) {
            sendEmail("ELN PMB Bridge - error", "A fatal error occurred: " + e.getMessage());
        }
    }

    private static void startService() throws Exception {
        Path pollPath = manager.getPollFolderPath();
        WatchKey watchKey;
        String newFileName;
        /**
         * WatchService monitors the poll folder specified in pmb.properties
         * A watchable object is registered with a particular type of event
         * When that event is detected, a key is added to the watch service que
         * The take method returns the watch key from the service when it becomes available
         * The event is processed and the newly created file name returned
         */
        WatchService service = pollPath.getFileSystem().newWatchService();
        pollPath.register(service, StandardWatchEventKinds.ENTRY_CREATE);

        while ((watchKey = service.take()) !=null) {
            List<WatchEvent<?>> watchEvents = watchKey.pollEvents();

            for (WatchEvent event : watchEvents) {
                newFileName = event.context().toString();
                try {
                    PrintRequest request = manager.makeRequestFromFile(newFileName);
                    Properties properties = manager.getPMBProperties();
                    PrintConfig printConfig = PrintConfig.loadConfig(properties);
                    PMBClient pmbClient = new PMBClient(printConfig);
                    pmbClient.print(request);
                    manager.archiveFile(newFileName);
                } catch (Exception e) {
                    sendErrorMessage(e);
                    manager.moveFileToErrorFolder(newFileName);
                }
            }
            watchKey.reset();
        }
    }

    private static void sendStartUpMessage() throws IOException, MessagingException {
        String currentTime = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        String message = String.format("Starting up ELN PMB Bridge service at %s, version number: %s",
                currentTime, CURRENT_VERSION);
        sendEmail("Starting up ELN PMB Service", message);
    }

    private static void sendErrorMessage(Exception e) throws IOException, MessagingException {
        String message = String.format("Error when trying to print labels via PrintMyBarcode from an polled ELN file. " +
                "Moving file to error folder. %s", e);
        sendEmail("ELN PMB Bridge - error", message);
    }

    private static void sendEmail(String subject, String startUpMessage) {
        try {
            EmailService emailManager = new EmailService(manager.getMailProperties());
            emailManager.sendEmail(subject, startUpMessage);
        } catch (Exception e){
            log.error(String.format("Failed to send email with subject %s", subject));
        }
    }

}