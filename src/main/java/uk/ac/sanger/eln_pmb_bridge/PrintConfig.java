package uk.ac.sanger.eln_pmb_bridge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class holding the pmb url and template id's for printing labels via PrintMyBarcode
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
     * Maps printer name to template id and retrieves pmb url from the pmb.properties file
     * @param properties the properties configured from pmb.properties
     */
    public static PrintConfig loadConfig(Properties properties) throws IOException {
        List<String> printers = new ArrayList<>();
        printers.addAll(properties.keySet()
                .stream()
                .filter(entry -> !entry.equals("pmb_url"))
                .filter(entry -> !entry.equals("poll_folder"))
                .filter(entry -> !entry.equals("archive_folder"))
                .filter(entry -> !entry.equals("error_folder"))
                .map(entry -> (String) entry)
                .collect(Collectors.toList()));

        String pmbURL = properties.getProperty("pmb_url", "");

        if (pmbURL.isEmpty() || printers.isEmpty()) {
            log.error("PMB url is missing or list of printers is empty in pmb.properties");
            throw new IOException("PMB url is missing or list of printers is empty in pmb.properties");
        }

        Map<String, Integer> printerTemplateIds = new HashMap<>();
        for (String printerName : printers) {
            String templateIdString = properties.getProperty(printerName.toLowerCase(), "").trim();
            Integer templateId = Integer.valueOf(templateIdString.trim());
            if (templateId!=null) {
                printerTemplateIds.put(printerName, templateId);
            } else {
                log.error(String.format("Printer name %s does not have template id in pmb.properties file", printerName));
                throw new IOException(String.format("Printer name %s does not have template id in pmb.properties file", printerName));
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
