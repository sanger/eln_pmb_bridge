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

    public PrintRequest makeRequestFromFile(String filename) throws FileNotFoundException {
        File file = findFile(filename);
        if (file == null) {
            return null;
        }
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
            String message = "";
            String errorMessage = (!printerExists ? message.concat(String.format("printer name %s doesn't exist.", printerName))
                    : message.concat("label list is empty"));
            String msg = String.format("Cannot make print request because: %s", errorMessage);
            log.error(msg);
            return null;
        }

        return new PrintRequest(printerName, labels);
    }

    /**
     * Moves a file either to the archive folder or error folder if the print job request is successful
     *   - finds the file from the given filename, adds a timestamp and moves it to specified folder
     * @param fileToMove the filename to move
     * @param folderToMoveTo the folder to move the file to
     */
    public void moveFileToFolder(String fileToMove, String folderToMoveTo) throws IOException {
        File sourceFile = findFile(fileToMove);
        String time = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        String newFileName = fileToMove.split("\\.")[0] + "_" + time + ".txt";
        File newFile = new File(folderToMoveTo + "/" + newFileName);

        boolean canArchive = checkFileWritable(sourceFile);
        if (!canArchive) {
            String msg = String.format("File \"%s\" does not have the correct permissions to archive.", fileToMove);
            log.debug(msg);
        }
        if (sourceFile != null) {
            Files.move(sourceFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            log.info(String.format("Moved file \"%s\" from %s to %s", sourceFile, sourceFile.toPath(), newFile.toPath()));
        }
    }

    public void setPMBProperties() throws Exception {
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

    public String getArchiveFolder() {
        return properties.getProperty("archive_folder");
    }

    public String getErrorFolder() {
        return properties.getProperty("error_folder");
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
        return null;
    }

    public File findPropertiesFile(String filename) throws FileNotFoundException {
        File f = new File(System.getProperty("user.dir") + File.separator + filename);
        if (f.isFile()) {
            return f;
        }
        String msg = String.format("Missing properties file %s.", filename);
        log.error(msg);
        throw new FileNotFoundException(msg);
    }

    /**
     * Checks if a file has the correct permissions allowing it to be archived
     */
    private boolean checkFileWritable(File sourceFile) {
        if (sourceFile.exists()) {
            if (!sourceFile.isFile() || !sourceFile.canWrite()) {
                log.debug("Failed to archive file " + sourceFile.getAbsolutePath());
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
