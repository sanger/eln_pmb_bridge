package uk.ac.sanger.eln_pmb_bridge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author hc6
 */
public class PrinterProperties extends PropertiesFileReader {
    private static final Logger log = LoggerFactory.getLogger(PrintRequestHelper.class);
    private static Properties properties;
    private static List<String> printerList;
    private static Map<String, Integer> printerTemplateIdList;

    public static void setProperties(String filePath) throws IOException {
        properties = loadFile(filePath);
        setPrinters();
        setPrinterTemplateList();
        log.info("Successfully set printer.properties.");
    }

    private static void setPrinters() {
        printerList = properties
                .keySet()
                .stream()
                .map(entry -> (String) entry)
                .collect(Collectors.toList());

        if (printerList.isEmpty()) {
            String msg = String.format("Cannot load print config because: %s", ErrorType.NO_PRINTERS.getMessage());
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    private static void setPrinterTemplateList() {
        Map<String, Integer> printerTemplateIds = new HashMap<>();
        for (String printerName : printerList) {

            String printerSize = properties.getProperty(printerName);
            if (printerSize.isEmpty()){
                throw new IllegalArgumentException(ErrorType.NO_PRINTER_SIZE.getMessage() + printerName);
            }

            Integer templateId = getTemplateIdForPrinterSize(printerSize);
            printerTemplateIds.put(printerName, templateId);
        }
        printerTemplateIdList = printerTemplateIds;
    }

    private static Integer getTemplateIdForPrinterSize(String printerSize) {
        String template_key = printerSize+"_template_id";
        Properties props = ELNPMBProperties.getProperties();
        String printerSizeTemplateId = props.getProperty(template_key);

        if (printerSizeTemplateId==null){
            throw new NullPointerException(ErrorType.NO_TEMPLATE_ID.getMessage());
        }
        return Integer.valueOf(printerSizeTemplateId);
    }

    public static Map<String, Integer> getPrinterTemplateIdList() {
        return printerTemplateIdList;
    }

    public static List<String> getPrinterList() {
        return printerList;
    }

    public static Integer getTemplateIdForPrinter(String printer) {
        return printerTemplateIdList.get(printer);
    }
}
