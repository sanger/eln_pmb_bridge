package uk.ac.sanger.eln_pmb_bridge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class holding PrintMyBarcode url and template id's for printing labels
 * @author hc6
 */
public class PrintConfig {
    private static final Logger log = LoggerFactory.getLogger(PrintConfig.class);

    private String pmbURL;
    private Map<String, Integer> printerTemplateIds;

    public PrintConfig(String pmbURL, Map<String, Integer> printerTemplateIds) {
        this.pmbURL = pmbURL;
        this.printerTemplateIds = printerTemplateIds;
    }

    /**
     * Gets PrintMyBarcode url from eln_pmb.properties
     * Gets the list of printers from printer.properties
     * Maps each printer name to respective thin/fat template id in eln_pmb.properties
     * Returns new PrintConfig object
     */
    public static PrintConfig loadConfig(PropertiesFileReader properties)
            throws InvalidPropertiesFormatException {

        Properties elnPmbProperties = properties.getElnPmbProperties();
        Properties printerProperties = properties.getPrinterProperties();

        List<String> printers = properties.getPrinterProperties()
                .keySet()
                .stream()
                .map(entry -> (String) entry)
                .collect(Collectors.toList());

        String pmbURL = elnPmbProperties.getProperty("pmb_url", "");
        String thin_template_id = elnPmbProperties.getProperty("thin_template_id", "");
        String fat_template_id = elnPmbProperties.getProperty("fat_template_id", "");

        String message = "";
        if (pmbURL.isEmpty()) {
            message += "\n\tPMB URL is missing.";
        }
        if (printers.isEmpty()) {
            message += "\n\tList of printers is empty in printer.properties.";
        }
        if (thin_template_id.isEmpty() || fat_template_id.isEmpty()) {
            message += "\n\tTemplate id's are missing from eln_pmb.properties.";
        }

        if (!message.isEmpty()) {
            String msg = String.format("Cannot load print config because: %s", message);
            throw new InvalidPropertiesFormatException(msg);
        }

        Map<String, Integer> printerTemplateIds = new HashMap<>();
        for (String printerName : printers) {
            String printerSize = printerProperties.getProperty(printerName.toLowerCase()).trim();
            if (printerSize.isEmpty()){
                throw new NullPointerException("Missing printer size for "+printerName+" from printer.properties");
            }
            String printerSizeTemplateId = elnPmbProperties.getProperty(printerSize+"_template_id");
            if (printerSizeTemplateId==null){
                throw new NullPointerException(printerSize+"_template_id does not exist in eln_pmb.properties");
            }
            Integer templateId = Integer.valueOf(printerSizeTemplateId);
            printerTemplateIds.put(printerName, templateId);
        }
        return new PrintConfig(pmbURL, printerTemplateIds);
    }

    public String getPmbURL() {
        return this.pmbURL;
    }

    public Map<String, Integer> getPrinterTemplateIds() {
        return this.printerTemplateIds;
    }

    public Integer getTemplateIdForPrinter(String printer) {
        return this.printerTemplateIds.get(printer);
    }

}
