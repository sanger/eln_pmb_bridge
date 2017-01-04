package uk.ac.sanger.eln_pmb_bridge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Helper class to
 *   - read and set property files
 *   - make a print request from a file
 *   - check r/w/e permissions of a file
 *   - archive a file
 * @author hc6
 */
public class FileManager {
    private static final Logger log = LoggerFactory.getLogger(FileManager.class);
    private Properties properties;

    public PrintRequest makeRequestFromFile(String filename) throws IOException {
        File file = findFile(filename);
        Scanner scanner = new Scanner(file);

        String firstLine = scanner.nextLine();
        scanner.nextLine();
        scanner.nextLine();

        /**
         * Retrieve requested printer name from the file and check printer name exists
         */
        Matcher matcher = Pattern.compile("PRN=\"([^\"]+)\"").matcher(firstLine);
        String printerName = "";
        if (matcher.find()) {
            printerName = matcher.group(1);
        }

        List<String> printers = getPrinters();
        boolean printerExists = printers.contains(printerName);
        List<Map<String, String>> fields = new ArrayList<>();

        /**
         * Gathers the rest of the data from the file and creates a label for each row in the file
         */
        while(scanner.hasNext()){
            String line = scanner.nextLine();
            String[] data = line.split(Pattern.quote("|"));

            Map<String, String> fieldMap = new HashMap<>();
            fieldMap.put("cell_line", data[0].trim());
            fieldMap.put("barcode", data[1].trim());
            fieldMap.put("barcode_text", data[1].trim());
            fieldMap.put("passage_number", data[2].trim());
            fieldMap.put("date", data[3].trim());
            fields.add(fieldMap);
        }

        List<PrintRequest.Label> labels = new ArrayList<>();
        for (Map<String, String> field : fields) {
            PrintRequest.Label label = new PrintRequest.Label(field);
            labels.add(label);
        }

        if (!printerExists || labels.isEmpty()){
            log.debug(String.format("Printer name %s doesn't exist or labels is empty", printerName));
            throw new IOException("Cannot make print request as printer doesn't exist or labels is empty");
        }

        PrintRequest request = new PrintRequest(printerName, labels);
        log.info(String.format("Made print request from file \"%s\"", filename));
        return request;
    }

    /**
     * Archives a file after sending a print job request
     *   - gets the archive folder path from properties
     *   - finds the file from the given filename, adds a timestamp and moves it to archive folder
     * @param filename the filename to archive
     */
    public void archiveFile(String filename) throws IOException {
        File sourceFile = findFile(filename);
        String archiveFolder = properties.getProperty("archive_folder", "");
        String archiveTime = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        String archiveFileName = filename.split("\\.")[0] + "_" + archiveTime + ".txt";
        File archiveFile = new File(archiveFolder + "/" + archiveFileName);

        boolean canArchive = checkFileWritable(sourceFile);
        if (!canArchive) {
            throw new IOException(String.format("File \"%s\" does not have the correct permissions to archive.", filename));
        }
        Files.move(sourceFile.toPath(), archiveFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        log.info(String.format("Archived file \"%s\" from %s to %s", filename, sourceFile.toPath(), archiveFile.toPath()));
    }

    public void errorFile(String filename) throws FileNotFoundException {
        File sourceFile = findFile(filename);
        String errorFolder = properties.getProperty("error_folder", "");
        String errorTime = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        String errorFileName = filename.split("\\.")[0] + "_" + errorTime + ".txt";
        File errorFile = new File(errorFolder + "/" + errorFileName);

        try {
            Files.move(sourceFile.toPath(), errorFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            log.info(String.format("Moved error file \"%s\" from %s to %s", filename, sourceFile.toPath(), errorFile.toPath()));
        } catch (Exception e) {
            log.debug(String.format("File \"%s\" does not have the correct permissions to more to error folder.", filename));
        }
    }

    public void setPMBProperties() throws IOException {
        File propertiesFile = findPropertiesFile("pmb.properties");
        FileInputStream fileInputStream = new FileInputStream(propertiesFile);

        this.properties = new Properties();
        this.properties.load(fileInputStream);

        log.info("Successfully set pmb.properties");
    }

    public Properties getPMBProperties() {
        return this.properties;
    }

    public Properties getMailProperties() throws IOException {
        File propertiesFile = findPropertiesFile("mail.properties");
        FileInputStream fileInputStream = new FileInputStream(propertiesFile);

        Properties mailProperties = new Properties();
        mailProperties.load(fileInputStream);
        return mailProperties;
    }

    public Path getPollFolderPath() {
        String pollFolder = properties.getProperty("poll_folder", "");
        return Paths.get(pollFolder);
    }

    /**
     * Tries to find a file with the given filename in various folders:
     *   - the current working directory
     *   - poll folder path
     *   - test folder path
     * @param filename the filename to find
     * @return the first matching file found, or throw exception if no such file is found
     */
    private File findFile(String filename) {
        String pollFolder = properties.getProperty("poll_folder", "");
        if (filename == null) {
            throw new IllegalArgumentException("Filename is null");
        }
        File f = new File(pollFolder + "/" + filename);
        if (f.isFile()) {
            return f;
        }
        f = new File(System.getProperty("user.dir") + File.separator + filename);
        if (f.isFile()) {
            return f;
        }
        f = new File(System.getProperty("user.dir") + File.separator + "data" + File.separator + filename);
        if (f.isFile()) {
            return f;
        }
        log.error(String.format("No file with name \"%s\" was found", filename));
        throw new IllegalArgumentException(String.format("No file with name \"%s\" was found", filename));
    }

    public File findPropertiesFile(String filename) {
        File f = new File(System.getProperty("user.dir") + File.separator + filename);
        if (f.isFile()) {
            return f;
        }
        log.error(String.format("%s file was not found", filename));
        throw new IllegalArgumentException(String.format("%s file was not found", filename));
    }

    /**
     * Checks if a file has the correct permissions allowing it to be archived
     */
    private boolean checkFileWritable(File sourceFile) {
        if (sourceFile.exists()) {
            if (!sourceFile.isFile()) {
                log.debug("Cannot archive file", String.format("File %s is not a regular file.",
                        sourceFile.getAbsolutePath()));
                return false;
            }
            if (!sourceFile.canWrite()) {
                log.debug("Cannot archive file", String.format("File %s does not have the correct permissions to archive.",
                        sourceFile.getAbsolutePath()));
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a list of printers configured in pmb.properties
     */
    public List<String> getPrinters() {
        return properties.keySet()
                .stream()
                .filter(entry -> !entry.equals("pmb_url"))
                .filter(entry -> !entry.equals("poll_folder"))
                .filter(entry -> !entry.equals("archive_folder"))
                .map(entry -> (String) entry)
                .collect(Collectors.toList());
    }
}
