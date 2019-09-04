package uk.ac.sanger.eln_pmb_bridge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.sanger.eln_pmb_bridge.PrinterConfig.Entry;

import java.io.IOException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * FileWatcher is the main logic class, where files are polled from the WatchService
 * A PrintRequest is made from the polled file
 * The request then gets sent to the printer service
 * If successful, the polled file moves to the archive folder, or the error folder
 * @author hc6
 */
public class FileWatcher {
    private static final Logger log = LoggerFactory.getLogger(FileWatcher.class);
    private static PrintRequestHelper printRequestHelper;
    private static EmailService emailService;
    private static WatchService service;
    private static PMBClient pmbClient;
    private static PrintService sprintClient;
    private static PrinterConfig printerConfig;
    private static boolean running = true;

    /**
     * A new WatchService monitors the polling folder specified in eln_pmb.properties
     * The polling directory is registered to watch for entry create events
     * When any create event is detected, a key is added to the watch service que
     * The take method returns the watch key from the que
     * The event is processed and the newly created filename returned
     */
    protected static void runService() throws Exception {
        registerService();
        startPolling();
    }

    protected static void registerService() throws Exception {
        printRequestHelper = new PrintRequestHelper();
        emailService = EmailService.getService();
        pmbClient = new PMBClient();
        sprintClient = new SPrintClient();
        printerConfig = PrinterConfig.getInstance();

        Path pollPath = Paths.get(ELNPMBProperties.getPollFolder());
        service = pollPath.getFileSystem().newWatchService();
        pollPath.register(service, StandardWatchEventKinds.ENTRY_CREATE);

        emailService.sendStartUpEmail();
        log.info("Successfully started service.");
    }

    private static void startPolling() throws Exception {
        while (running) {
            WatchKey watchKey = service.take();

            List<WatchEvent<?>> watchEvents = watchKey.pollEvents();

            for (WatchEvent event : watchEvents) {
                String newFileName = event.context().toString();
                log.info(String.format("New file %s in polling folder.", newFileName));

                if (newFileName.endsWith("_TEMP")) {
                    log.info("Ignoring {}", newFileName);
                    continue;
                }

                Path pollFile = Paths.get(ELNPMBProperties.getPollFolder()+newFileName);
                try {
                    PrintRequest request = printRequestHelper.makeRequestFromFile(pollFile);
                    String printerName = request.getPrinterName();
                    Entry printerEntry = printerConfig.getPrinterConfig(printerName);
                    getPrintService(printerEntry.service).print(request);
                    moveFileToFolder(pollFile, ELNPMBProperties.getArchiveFolder());
                } catch (Exception e) {
                    log.error(ErrorType.RECOVERABLE.getMessage(), e);
                    moveFileToFolder(pollFile, ELNPMBProperties.getErrorFolder());
                    String subject = ErrorType.ELN_PMB_SUBJECT.getMessage() + ErrorType.RECOVERABLE.getMessage();
                    emailService.sendErrorEmail(subject, e);
                }
            }
            watchKey.reset();
        }
    }

    private static PrintService getPrintService(PrinterConfig.Service serviceType) {
        switch (serviceType) {
            case PMB: return pmbClient;
            case SPRINT: return sprintClient;
        }
        throw new IllegalArgumentException(ErrorType.UNKNOWN_PRINT_SERVICE.getMessage()+serviceType);
    }

    private static void moveFileToFolder(Path fileToMove, String folderToMoveTo) throws IOException {
        String fileName = fileToMove.getFileName().toString();
        String newFileName = renameFile(fileName);

        try {
            Files.move(fileToMove, Paths.get(folderToMoveTo+newFileName));
            log.info(String.format("Successfully moved file \"%s\" to %s", fileToMove, folderToMoveTo+newFileName));
        } catch (IOException e) {
            String msg = String.format("Failed to move file \"%s\" to %s", fileToMove, folderToMoveTo+newFileName);
            running = false;
            throw new IOException(msg, e);
        }
    }

    private static String renameFile(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex <= 0) {
            dotIndex = fileName.length();
        }
        String time = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

        return fileName.replace(" ", "_").substring(0, dotIndex) + "_" + time + ".txt";
    }
}
