
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
 *   - load property files when the service is started
 *   - get properties
 *   - find files
 *   - move files
 * @author hc6
 */
public class PropertiesFileReader {
    private static final Logger log = LoggerFactory.getLogger(PrintRequestHelper.class);
    private Properties elnPmbProperties;
    private Properties printerProperties;

    protected void loadProperties() throws Exception {
        File elnPmbPropertiesFile = findFile("eln_pmb.properties");
        FileInputStream elnPmbFileInputStream = new FileInputStream(elnPmbPropertiesFile);
        this.elnPmbProperties = new Properties();
        this.elnPmbProperties.load(elnPmbFileInputStream);

        File printerPropertiesFile = findFile("printer.properties");
        FileInputStream printerFileInputStream = new FileInputStream(printerPropertiesFile);
        this.printerProperties = new Properties();
        this.printerProperties.load(printerFileInputStream);

        log.info("Successfully set eln_pmb.properties");
    }

    protected Properties getElnPmbProperties() {
        return this.elnPmbProperties;
    }

    protected Properties getPrinterProperties(){
        return this.printerProperties;
    }

    /**
     * Mail properties can be updated while service is running
     */
    protected Properties getMailProperties() throws IOException {
        File mailPropertiesFile = findFile("mail.properties");
        FileInputStream mailFileInputStream = new FileInputStream(mailPropertiesFile);

        Properties mailProperties = new Properties();
        mailProperties.load(mailFileInputStream);
        return mailProperties;
    }

    protected Path getPollFolderPath() {
        String pollFolder = elnPmbProperties.getProperty("poll_folder", "");
        return Paths.get(pollFolder);
    }

    protected String getArchiveFolder() {
        return elnPmbProperties.getProperty("archive_folder");
    }

    protected String getErrorFolder() {
        return elnPmbProperties.getProperty("error_folder");
    }

    /**
     * Returns a list of printers configured in printer.properties
     */
    protected List<String> getPrinters() {
        return printerProperties.keySet()
                .stream()
                .map(entry -> (String) entry)
                .collect(Collectors.toList());
    }

    /**
     * Tries to find a file with the given filename in various folders
     * @param filename the filename to find
     * @return the first matching file found, or throw exception if no such file is found
     */
    protected File findFile(String filename) throws FileNotFoundException {
//        Search in property folder for property files
        File f = new File("./properties_folder/"+filename);
        if (f.isFile()) {
            return f;
        }
//        Search in poll folder from eln_pmb.properties for polling
        String pollFolder = elnPmbProperties.getProperty("poll_folder", "");
        f = new File(pollFolder + "/" + filename);
        if (f.isFile()) {
            return f;
        }
//        Search in data_test folder for  testing
        f = new File("/Users/hc6/eln_pmb_bridge/data_test/" + filename);
        if (f.isFile()) {
            return f;
        }
//       Search in the the archive folder for testing moving a file
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
    protected void moveFileToFolder(String fileToMove, String folderToMoveTo) throws IOException {
        File sourceFile;
        try {
            sourceFile = findFile(fileToMove);
        } catch (FileNotFoundException e) {
            log.debug(String.format("Failed to move file \"%s\"", fileToMove));
            throw new FileNotFoundException(String.format("Failed to move file \"%s\" to \"%s\"", fileToMove, folderToMoveTo));
        }
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
