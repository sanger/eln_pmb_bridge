
package uk.ac.sanger.eln_pmb_bridge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Helper class to
 *   - load all property files when the service is started
 *   - get properties
 *   - find files and
 *   - make a print request from a file
 *   - check r/w/e permissions of a file
 *   - archive a file
 * @author hc6
 */
public class PropertiesFileReader {
    private static final Logger log = LoggerFactory.getLogger(PrintRequestHelper.class);
    private Properties elnPmbProperties;
    private Properties printerProperties;

    public void loadProperties() throws Exception {
        File elnPmbPropertiesFile = findPropertiesFile("eln_pmb.properties");
        FileInputStream elnPmbFileInputStream = new FileInputStream(elnPmbPropertiesFile);
        this.elnPmbProperties = new Properties();
        this.elnPmbProperties.load(elnPmbFileInputStream);

        File printerPropertiesFile = findPropertiesFile("printer.properties");
        FileInputStream printerFileInputStream = new FileInputStream(printerPropertiesFile);
        this.printerProperties = new Properties();
        this.printerProperties.load(printerFileInputStream);

        log.info("Successfully set eln_pmb.properties");
    }

    public Properties getElnPmbProperties() {
        return this.elnPmbProperties;
    }

    public Properties getPrinterProperties(){
        return this.printerProperties;
    }

    /**
     * Mail properties can be updated while service is running
     */
    public Properties getMailProperties() throws IOException {
        File mailPropertiesFile = findPropertiesFile("mail.properties");
        FileInputStream mailFileInputStream = new FileInputStream(mailPropertiesFile);

        Properties mailProperties = new Properties();
        mailProperties.load(mailFileInputStream);
        return mailProperties;
    }

    public Path getPollFolderPath() {
        String pollFolder = elnPmbProperties.getProperty("poll_folder", "");
        return Paths.get(pollFolder);
    }

    public String getArchiveFolder() {
        return elnPmbProperties.getProperty("archive_folder");
    }

    public String getErrorFolder() {
        return elnPmbProperties.getProperty("error_folder");
    }

    /**
     * Returns a list of printers configured in printer.properties
     */
    public List<String> getPrinters() {
        return printerProperties.keySet()
                .stream()
                .map(entry -> (String) entry)
                .collect(Collectors.toList());
    }

    public File findPropertiesFile(String filename) throws FileNotFoundException {
        File f = new File("./properties_folder/"+filename);
        if (f.isFile()) {
            return f;
        }
        String msg = String.format("Missing properties file %s.", filename);
        throw new FileNotFoundException(msg);
    }

    /**
     * Tries to find a file with the given filename in various folders:
     *   - the current working directory
     *   - poll folder path
     * @param filename the filename to find
     * @return the first matching file found, or throw exception if no such file is found
     */
    public File findFile(String filename) throws FileNotFoundException {
        String pollFolder = elnPmbProperties.getProperty("poll_folder", "");
//        Poll files via poll_folder path from eln_pmb.properties for staging
        File f = new File(pollFolder + "/" + filename);
        if (f.isFile()) {
            return f;
        }
//        Find a file in the data_test directory for testing
        f = new File("/Users/hc6/eln_pmb_bridge/data_test/" + filename);
        if (f.isFile()) {
            return f;
        }
//        Find a file in the archive_folder for testing moveFileToFolder successful
        f = new File("/Users/hc6/eln_pmb_bridge/archive_folder/" + filename);
        if (f.isFile()) {
            return f;
        }
        log.error(String.format("No file with name \"%s\" was found", filename));
        throw new FileNotFoundException(String.format("File %s does not exist", filename));
    }

    /**
     * Moves a file either to the archive folder or error folder if the print job request is successful
     *   - finds the file from the given filename
     *   - replaces any whitespace with and underscore
     *   - adds a timestamp and moves it to specified folder
     * @param fileToMove the filename to move
     * @param folderToMoveTo the folder to move the file to
     */
    public void moveFileToFolder(String fileToMove, String folderToMoveTo) throws IOException {
        File sourceFile = findFile(fileToMove);
        String time = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        int dotIndex = fileToMove.lastIndexOf('.');
        if (dotIndex <= 0) {
            dotIndex = fileToMove.length();
        }
        String newFileName = fileToMove.replaceAll(" ", "_").substring(0, dotIndex) + "_" + time + ".txt";
        File newFile = new File(folderToMoveTo + "/" + newFileName);

        if (sourceFile != null) {
            Files.move(sourceFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            log.info(String.format("Moved file \"%s\" from %s to %s", sourceFile, sourceFile.toPath(), newFile.toPath()));
        }
    }

}
