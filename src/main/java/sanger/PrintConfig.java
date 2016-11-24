package sanger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;


/**
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

    public static PrintConfig loadConfig() {
        PrintConfig p;
        try {
            p = loadProperties();
            log.info("Loaded print configuration properties");
        } catch (Exception e) {
            log.error("Failed to load print configuration properties");
            p = null;
        }
        return p;
    }

    public static PrintConfig loadProperties() throws IOException {
        FileManager fileManager = new FileManager();
        Properties properties = fileManager.readPropertiesFile("pmb.properties");

        String pmbURL = properties.getProperty("pmb_url", "");
        if (pmbURL.isEmpty()) {
            log.error("No pmb url supplied in pmb.properties");
            throw new IOException("No pmb url supplied in pmb.properties");
        }
        List<String> printers = fileManager.getPrintersFromFile("printers.properties");
        Map<String, Integer> printerTemplateIds = new HashMap<>();

        for (String printerName : printers) {
            String templateIdString = properties.getProperty(printerName.toLowerCase(), "").trim();
            if (!templateIdString.isEmpty()) {
                Integer templateId = Integer.valueOf(templateIdString.trim());
                printerTemplateIds.put(printerName, templateId);
            } else {
                log.error("Printer name {} does not have template id in pmb.properties file", printerName);
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
