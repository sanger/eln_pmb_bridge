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
 * @author hc6
 */
public class FileManager {
    private static final Logger log = LoggerFactory.getLogger(FileManager.class);
    private Properties properties;

    public PrintRequest makeRequestFromFile(String filename) throws IOException {
        File file = findFile(filename);
        Scanner scanner = new Scanner(file);

        List<Map<String, String>> fields = new ArrayList<>();

        String firstLine = scanner.nextLine();
        scanner.nextLine();
        scanner.nextLine();

        Matcher matcher = Pattern.compile("PRN=\"([^\"]+)\"").matcher(firstLine);
        String printerName = "";
        if (matcher.find()) {
            printerName = matcher.group(1);
        }

        boolean printerExists = properties.keySet()
                .stream()
                .filter(entry -> !entry.equals("pmb_url"))
                .filter(entry -> !entry.equals("poll_folder"))
                .filter(entry -> !entry.equals("archive_folder"))
                .map(entry -> (String) entry)
                .collect(Collectors.toList())
                .contains(printerName);

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
            throw new IOException("Cannot make print request as printer doesnt exist or labels is empty");
        }

        PrintRequest request = new PrintRequest(printerName, labels);
        log.info("Made print request from file {}", filename);
        return request;
    }

    public void archiveFile(String filename) throws IOException {
        File sourceFile = findFile(filename);
        String archiveFolder = properties.getProperty("archive_folder", "");

        String archiveTime = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        String archiveFileName = filename.split("\\.")[0] + "_" + archiveTime + ".txt";

        File archiveFile = new File(archiveFolder + "/" + archiveFileName);
        Files.move(sourceFile.toPath(), archiveFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        log.info(String.format("Archived file %s from %s to %s",
                filename, sourceFile.toPath(), archiveFile.toPath()));
    }

    public Properties getPMBProperties() {
        return this.properties;
    }

    public void setPMBProperties() throws IOException {
        File propertiesFile = findPropertiesFile();
        FileInputStream fileInputStream = new FileInputStream(propertiesFile);

        this.properties = new Properties();
        this.properties.load(fileInputStream);

        log.info("Successfully set pmb properties");
    }

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
        log.error("No file with name {} was found", filename);
        throw new IllegalArgumentException("No file was found");
    }

    public File findPropertiesFile() {
        File f = new File(System.getProperty("user.dir") + File.separator + "pmb.properties");
        if (f.isFile()) {
            return f;
        }
        log.error("PMB properties file was not found");
        throw new IllegalArgumentException("PMB properties file was not found");
    }

    public Path getPollFolderPath() {
        String pollFolder = properties.getProperty("poll_folder", "");
        return Paths.get(pollFolder);
    }

}
