package sanger.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sanger.parameters.PrintRequest;

import java.io.*;
import java.util.*;

/**
 * @author hc6
 */
public class FileManager {
    private static final Logger log = LoggerFactory.getLogger(FileManager.class);

    public Properties readPropertiesFile(String fileName) throws FileNotFoundException {
        File file = findFile(fileName);
        Properties properties = new Properties();
        if (file!=null) {
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                properties.load(fileInputStream);
                log.info("Successfully read properties from file");
            } catch (IOException e) {
                log.error("Failed to read properties from file");
                e.printStackTrace();
            }
        }
        return properties;
    }

    public PrintRequest makeRequestFromFile(String filename) throws IOException {
        File file = findFile(filename);

        PrintRequest request = null;
        if (file!=null){
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                String s = scanner.next();
                String[] values = s.split("[|]");

                Map<String, String> fieldMap = new HashMap<>();
                fieldMap.put("cell_line", values[0]);
                fieldMap.put("barcode", values[1]);

                List<PrintRequest.Label> labels = new ArrayList<>();
                PrintRequest.Label label = new PrintRequest.Label(fieldMap);
                labels.add(label);

                request = new PrintRequest("d340bc", labels);
                log.info("Made print request from file {}", filename);
            }
        }
        return request;
    }

    public List<String> getPrintersFromFile(String filename) throws FileNotFoundException {
        File file = findFile(filename);
        List<String> printers = new ArrayList<>();

        if (file!=null){
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                String s = scanner.next();
                String[] values = s.split("[\n]");
                Collections.addAll(printers, values);
            }
            log.info("Found printers {}", printers.toString());
        }
        return printers;
    }

    private File findFile(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Filename is null");
        }
        File f = new File(System.getProperty("user.dir") + File.separator + filename);
        if (f.isFile()) {
            return f;
        }
        log.error("No file with name {} was found", filename);
        return null;
    }

}
