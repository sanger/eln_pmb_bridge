package uk.ac.sanger.eln_pmb_bridge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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

        List<String> printers = properties.getPrinters();

        String pmbURL = elnPmbProperties.getProperty("pmb_url", "");
        String thin_template_id = elnPmbProperties.getProperty("thin_template_id", "");
        String fat_template_id = elnPmbProperties.getProperty("fat_template_id", "");

        if (pmbURL.isEmpty() || printers.isEmpty() || thin_template_id.isEmpty() || fat_template_id.isEmpty()) {
            String message = "";
            if (pmbURL.isEmpty()) {
                message = message.concat("PMB URL is missing");
            } else if (pmbURL.isEmpty()) {
                message = message.concat("list of printers is empty in printer.properties");
            } else if (thin_template_id.isEmpty() || fat_template_id.isEmpty()) {
                message = message.concat("template id's are missing from eln_pmb.properties");
            }
            String msg = String.format("Cannot load print config because: %s", message);
            log.error(msg);
            throw new InvalidPropertiesFormatException(msg);
        }

        Map<String, Integer> printerTemplateIds = new HashMap<>();
        for (String printerName : printers) {
            String printerSize = printerProperties.getProperty(printerName.toLowerCase(), "").trim();
            String templateIdString = "";
            switch (printerSize) {
                case "thin":
                    templateIdString = elnPmbProperties.getProperty("thin_template_id");
                    break;
                case "fat":
                    templateIdString = elnPmbProperties.getProperty("fat_template_id");
                    break;
            }
            Integer templateId = Integer.valueOf(templateIdString.trim());
            if (templateId!=null) {
                printerTemplateIds.put(printerName, templateId);
            }
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
