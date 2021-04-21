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

    public enum EnvironmentMode {
        TEST("devel"),
        DEVEL("devel"),
        WIP("devel"),
        UAT("devel"),
        PROD("prod"),
        ;

        public final String property_folder;

        EnvironmentMode(String property_folder) {
            this.property_folder = property_folder;
        }
    }

    public static void main(String[] args) throws Exception {
        // When a host has both IPv4 and IPv6 addresses, change preference to use IPv6 addresses over IPv4
        System.setProperty("java.net.preferIPv6Addresses", "true");
        EnvironmentMode mode = readMode(args);
        boolean sendStartupEmail = readSendStartupEmail(args);

        EmailService emailService = EmailService.createService(mode, sendStartupEmail);
        try {
            createFolders();
            setProperties(mode);
            FileWatcher.runService();
        } catch (Exception e) {
            log.error(ErrorType.FATAL.getMessage(), e);
            emailService.sendErrorEmail(ErrorType.ELN_PMB_SUBJECT.getMessage() + ErrorType.FATAL.getMessage(), e);
        }
    }

    private static EnvironmentMode readMode(String[] args) {
        EnvironmentMode mode = null;
        for (String arg : args) {
            arg = arg.toUpperCase();
            if (arg.startsWith("ENV=")) {
                String modeString = arg.substring(4).trim();
                try {
                    mode = EnvironmentMode.valueOf(modeString);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException(ErrorType.UNKNOWN_ENV_MODE.getMessage());
                }
            }
        }
        if (mode==null) {
            throw new IllegalArgumentException(ErrorType.NO_ENV_MODE_IN_MAIN_ARGS.getMessage());
        }
        log.info(String.format("Successfully set environment mode %s.", mode));
        return mode;
    }

    private static boolean readSendStartupEmail(String[] args) {
        return Arrays.stream(args).noneMatch("--nostartemail"::equalsIgnoreCase);
    }

    /**
     *  ELNPMBProperties have to be set before the PrinterProperties
     */
    private static void setProperties(EnvironmentMode mode) throws IOException {
        String folder = String.format("./properties_folder/%s/", mode.property_folder);
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