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
 * @author hc6
 */
public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    protected static EnvironmentMode startMode = EnvironmentMode.DEVEL;

    public enum EnvironmentMode {
        TEST("devel"),
        DEVEL("devel"),
        WIP("devel"),
        UAT("devel"),
        PROD("prod");

        public final String property_folder;

        EnvironmentMode(String property_folder) {
            this.property_folder = property_folder;
        }
    }

    public static void main(String[] args) throws Exception {
        // When a host has both IPv4 and IPv6 addresses, change preference to use IPv6 addresses over IPv4
        System.setProperty("java.net.preferIPv6Addresses", "true");
        setEnvironmentMode(args);

        EmailService.setService(startMode);
        EmailService emailService = EmailService.getService();
        try {
            createFolders();
            setProperties();
            FileWatcher.runService();
        } catch (Exception e) {
            log.error(ErrorType.FATAL.getMessage(), e);
            emailService.sendErrorEmail(ErrorType.ELN_PMB_SUBJECT.getMessage() + ErrorType.FATAL.getMessage(), e);
        }
    }

    public static void setEnvironmentMode(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException(ErrorType.NO_ENV_MODE_IN_MAIN_ARGS.getMessage());
        } else {
            for (String arg : args) {
                arg = arg.toUpperCase();
                if (arg.startsWith("ENV=")) {
                    String modeString = arg.substring(4).trim();
                    try {
                        startMode = EnvironmentMode.valueOf(modeString);
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException(ErrorType.UNKNOWN_ENV_MODE.getMessage());
                    }
                }
            }
        }
        log.info(String.format("Successfully set environment mode %s.", startMode));
    }

    /**
     *  ELNPMBProperties have to be set before the PrinterProperties
     */
    private static void setProperties() throws IOException {
        String folder = String.format("./properties_folder/%s/", startMode.property_folder);
        MailProperties.setProperties(folder + "mail.properties");
        ELNPMBProperties.setProperties(folder + "eln_pmb.properties");
        SPrintConfig.initialise(folder + "sprint.properties");
        PrinterConfig.initialise(folder + "printer.properties");
    }

    /**
     *  TODO: do this in the building of jar/ control script
     */
    private static void createFolders() throws IOException {
        List<String> directories = Arrays.asList("poll_folder", "archive_folder", "error_folder", "properties_folder");

        for (String directory : directories) {
            Path directoryPath = Paths.get(directory);
            if (!Files.exists(directoryPath)) {
                createFolder(directoryPath);
            }
        }
        log.info("Successfully created directories if they didn't already exist.");
    }

    private static void createFolder(Path directoryPath) throws IOException {
        try {
            Files.createDirectory(directoryPath);
        } catch (IOException e) {
            String msg = ErrorType.FAILED_FOLDER_CREATION.getMessage() + directoryPath;
            log.debug(msg, e);
            throw new IOException(msg, e);
        }
    }

}