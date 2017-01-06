package uk.ac.sanger.eln_pmb_bridge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class to make a PrintRequest
 * @author hc6
 */
public class PrintRequestHelper {
    private static final Logger log = LoggerFactory.getLogger(PrintRequestHelper.class);

    public PrintRequest makeRequestFromFile(File file, List<String> printers) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        String firstLine = scanner.nextLine();
        scanner.nextLine();
        scanner.nextLine();

        /**
         * Gets the printer name from the polled file and check printer name exists
         */
        Matcher matcher = Pattern.compile("PRN=\"([^\"]+)\"").matcher(firstLine);
        String printerName;
        if (matcher.find()) {
            printerName = matcher.group(1);
        } else {
            throw new IllegalArgumentException("No printer name is given in print request");
        }

        boolean printerExists = printers.contains(printerName);
        List<Map<String, String>> fields = new ArrayList<>();

        /**
         * Gathers the rest of the data from the file and creates a label for each row in the file
         * TODO: Automate reading of column headers to populate fieldMap
         */
        while(scanner.hasNext()){
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
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
            String msg = String.format("Cannot make print request because %s", errorMessage);
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }

        return new PrintRequest(printerName, labels);
    }

}
