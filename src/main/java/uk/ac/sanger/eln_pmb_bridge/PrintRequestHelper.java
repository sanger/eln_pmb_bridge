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
        if (printers.isEmpty()) {
            String msg = "Cannot make print request because printer list is empty.";
            throw new IllegalArgumentException(msg);
        }
        Scanner fileData = new Scanner(file);
        String firstLine = fileData.nextLine();
        fileData.nextLine();

        List<PrintRequest.Label> labels = createLabels(fileData);

        String printerName = getPrinterName(firstLine);
        boolean printerExists = printers.contains(printerName);

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

    /**
     * Creates a label for each row in the file, mapping the data against the columns headers
     */
    protected List<PrintRequest.Label> createLabels(Scanner fileData) {
        String columnHeadingLine = fileData.nextLine();
        String[] columnHeadings = columnHeadingLine.split(Pattern.quote("|"));

        List<String> columns = new ArrayList<>();
        for (String ch : columnHeadings) {
            ch = ch.trim().toLowerCase().replaceAll("\\s+", "_");
            columns.add(ch);
        }

        List<Map<String, String>> fields = new ArrayList<>();

        while(fileData.hasNext()){
            String line = fileData.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] data = line.split(Pattern.quote("|"));

            Map<String, String> fieldMap = new HashMap<>();
            for (int i = 0; i < data.length; i++) {
                if (columns.get(i).equals("barcode")) {
                    fieldMap.put(columns.get(i), data[i].trim());
                    fieldMap.put(columns.get(i).concat("_text"), data[i].trim());
                }
                fieldMap.put(columns.get(i), data[i].trim());
            }
            fields.add(fieldMap);
        }

        List<PrintRequest.Label> labels = new ArrayList<>();
        for (Map<String, String> field : fields) {
            PrintRequest.Label label = new PrintRequest.Label(field);
            labels.add(label);
        }
        return labels;
    }

    /**
     * Gets the printer name from the file
     */
    protected String getPrinterName(String firstLine) {
        Matcher matcher = Pattern.compile("PRN=\"([^\"]+)\"").matcher(firstLine);
        String printerName;
        if (matcher.find()) {
            printerName = matcher.group(1);
        } else {
            throw new IllegalArgumentException("No printer name is given in print request");
        }
        return printerName;
    }

}
