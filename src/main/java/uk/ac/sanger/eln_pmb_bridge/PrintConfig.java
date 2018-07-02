package uk.ac.sanger.eln_pmb_bridge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.InvalidPropertiesFormatException;
import java.util.Map;

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
    public static PrintConfig loadConfig()
            throws InvalidPropertiesFormatException {

        String pmbURL = ELNPMBProperties.getPMBURL();
        Map<String, Integer> printerTemplateIds = PrinterProperties.getPrinterTemplateIdList();

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
