package uk.ac.sanger.eln_pmb_bridge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author hc6
 */
public class FileManager {
    private static final Logger log = LoggerFactory.getLogger(FileManager.class);

    public PrintRequest makeRequestFromFile(String filename) throws IOException {
        File file = findFile(filename);

        List<PrintRequest.Label> labels = new ArrayList<>();
        Scanner scanner = new Scanner(file);

        while (scanner.hasNextLine()) {
            String s = scanner.nextLine();
            String[] values = s.split(Pattern.quote("|"));

            Map<String, String> fieldMap = new HashMap<>();
            fieldMap.put("cell_line", values[0]);
            fieldMap.put("barcode", values[1]);

            PrintRequest.Label label = new PrintRequest.Label(fieldMap);
            labels.add(label);
        }
//          printer name might come from file
        String printerName = "d304bc";
        if (printerName.isEmpty() || labels.isEmpty()){
            throw new IOException("Cannot make print request with empty fields");
        }

        PrintRequest request = new PrintRequest(printerName, labels);
        log.info("Made print request from file {}", filename);
        return request;
    }

    public Properties readPropertiesFile(String fileName) throws IOException {
        File file = findFile(fileName);
        Properties properties = new Properties();

        FileInputStream fileInputStream = new FileInputStream(file);
        properties.load(fileInputStream);

        log.info("Successfully read properties from file");
        return properties;
    }

    private File findFile(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Filename is null");
        }
        File f = new File(System.getProperty("user.dir") + File.separator + filename);
        if (f.isFile()) {
            return f;
        }
        f = new File(System.getProperty("user.home") + "/Desktop/" + filename);
        if (f.isFile()) {
            return f;
        }
        log.error("No file with name {} was found", filename);
        throw new IllegalArgumentException("No file was found");
    }

}
