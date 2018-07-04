package uk.ac.sanger.eln_pmb_bridge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;

/**
 * ELN PMB Bridge is an application that polls files from (current: web-cgap-idbstest-01)
 * Builds a print request from the file
 * Sends a print job request to PrintMyBarcode to print created labels
 */
public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        /**
         * When a host has both IPv4 and IPv6 addresses, change preference to use IPv6 addresses over IPv4
         */
        System.setProperty("java.net.preferIPv6Addresses", "true");

        try {
            createFolders();
            setProperties();
            FileWatcher.startService();
        } catch (Exception e) {
            log.error(ErrorType.FATAL.getMessage(), e);
            EmailService.sendErrorEmail(ErrorType.ELN_PMB_SUBJECT.getMessage() + ErrorType.FATAL.getMessage(), e);
        }
    }
    /**
     *  ELNPMBProperties have to be set before the PrinterProperties
     */
    private static void setProperties() throws IOException {
        ELNPMBProperties.setProperties("./properties_folder/eln_pmb.properties");
        PrinterProperties.setProperties("./properties_folder/printer.properties");
        MailProperties.setProperties("./properties_folder/mail.properties");

        log.info("Successfully set eln_pmb.properties, printer.properties and mail.properties.");
    }

    /**
     *  TODO: do this in the building of jar/ control script
     */
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
                    log.debug(msg, e);
                }
            }
        }
        log.info("Successfully created directories if they didn't already exist.");
    }

}