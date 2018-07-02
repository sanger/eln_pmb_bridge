package uk.ac.sanger.eln_pmb_bridge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * ELN PMB Bridge is an application that polls files from (current: web-cgap-idbstest-01)
 * Builds a print request from the file
 * Sends a print job request to PrintMyBarcode to print created labels
 */
public class Main {
    private static final Logger log = LoggerFactory.getLogger(PrintConfig.class);

    public static void main(String[] args) throws Exception {
        /**
         * When a host has both IPv4 and IPv6 addresses, change preference to use IPv6 addresses over IPv4
         */
        System.setProperty("java.net.preferIPv6Addresses", "true");
        try {
            createFolders();
            setProperties();
            sendStartUpEmail();
            startService();
        } catch (Exception e) {
            log.error(ErrorType.FATAL.getMessage(), e);
            sendErrorEmail(ErrorType.ELN_PMB_SUBJECT.getMessage() + ErrorType.FATAL.getMessage(), e);
        }
    }

    //        ELNPMBProperties have to be set before the PrinterProperties
    private static void setProperties() throws IOException {
        ELNPMBProperties.setProperties();
        PrinterProperties.setProperties();
        MailProperties.setProperties();

        log.info("Successfully set eln_pmb.properties, printer.properties and mail.properties.");
    }

    private static void startService() throws Exception {
        PrintRequestHelper printRequestHelper = new PrintRequestHelper();
        PrintConfig printConfig = PrintConfig.loadConfig();
        /**
         * A new WatchService monitors the polling folder specified in eln_pmb.properties
         * The polling directory is registered to watch for entry create events
         * When any create event is detected, a key is added to the watch service que
         * The take method returns the watch key from the que
         * The event is processed and the newly created filename returned
         */
        Path pollPath = Paths.get(ELNPMBProperties.getPollFolder());
        WatchService service = pollPath.getFileSystem().newWatchService();
        pollPath.register(service, StandardWatchEventKinds.ENTRY_CREATE);

        while (true) {
            WatchKey watchKey = service.take();
            List<WatchEvent<?>> watchEvents = watchKey.pollEvents();
            PMBClient pmbClient = new PMBClient(printConfig);

            for (WatchEvent event : watchEvents) {
                String newFileName = event.context().toString();
                Path pollFile = Paths.get(ELNPMBProperties.getPollFolder()+newFileName);
                try {
                    PrintRequest request = printRequestHelper.makeRequestFromFile(pollFile);
                    pmbClient.print(request);
                    moveFileToFolder(pollFile, ELNPMBProperties.getArchiveFolder());
                } catch (Exception e) {
                    log.error(ErrorType.RECOVERABLE.getMessage(), e);
                    moveFileToFolder(pollFile, ELNPMBProperties.getErrorFolder());
                    sendErrorEmail(ErrorType.ELN_PMB_SUBJECT.getMessage() + ErrorType.RECOVERABLE.getMessage(), e);
                }
            }
            watchKey.reset();
        }
    }

    private static void sendStartUpEmail() {
        String currentTime = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        String message = String.format("Starting up ELN PMB Bridge Service at %s", currentTime);
        sendEmail("Starting up ELN PMB Bridge Service", message);
    }

    private static void sendErrorEmail(String subject, Exception e) {
        String message = ErrorType.MOVE_TO_ERROR_FOLDER.getMessage() + String.format("Exception: %s", e);
        sendEmail(subject, message);
    }

    private static void sendEmail(String subject, String message) {
        try {
            EmailService emailManager = new EmailService();
            emailManager.sendEmail(subject, message);
        } catch (Exception e){
            log.error(ErrorType.FAILED_EMAIL + String.format("Subject:  %s", subject));
        }
    }

    private static void moveFileToFolder(Path fileToMove, String folderToMoveTo) throws IOException {
        String fileName = fileToMove.getFileName().toString();
        String newFileName = renameFile(fileName);

        Files.move(fileToMove, Paths.get(folderToMoveTo+newFileName));
        log.info(String.format("Moved file \"%s\" to %s", fileToMove, folderToMoveTo+newFileName));
    }

    private static String renameFile(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex <= 0) {
            dotIndex = fileName.length();
        }
        String time = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

        return fileName.replace(" ", "_").substring(0, dotIndex) + "_" + time + ".txt";
    }

    private static void createFolders() {
        List<String> directories = Arrays.asList("poll_folder", "archive_folder", "error_folder", "properties_folder");
        for (String directory : directories) {
            Path directoryPath = Paths.get(directory);
            if (!Files.exists(directoryPath)) {
                try {
                    Files.createDirectory(directoryPath);
                } catch (IOException e) {
                    String msg = ErrorType.FAILED_FOLDER_CREATION.getMessage() + directoryPath
                            + String.format("Exception: %s", e);
                }
            }
        }
    }

}