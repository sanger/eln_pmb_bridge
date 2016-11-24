package uk.ac.sanger.eln_pmb_bridge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


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

    public static PrintConfig loadConfig() throws IOException {
        FileManager fileManager = new FileManager();
        Properties properties = fileManager.readPropertiesFile("pmb.properties");

        List<String> printers = new ArrayList<>();
        printers.addAll(properties.keySet()
                .stream()
                .filter(entry -> !entry.equals("pmb_url"))
                .map(entry -> (String) entry)
                .collect(Collectors.toList()));

        String pmbURL = properties.getProperty("pmb_url", "");

        if (pmbURL.isEmpty() || printers.isEmpty()) {
            log.error("Config is missing in pmb.properties");
            throw new IOException("Config is missing in pmb.properties");
        }

        Map<String, Integer> printerTemplateIds = new HashMap<>();
        for (String printerName : printers) {
            String templateIdString = properties.getProperty(printerName.toLowerCase(), "").trim();
            Integer templateId = Integer.valueOf(templateIdString.trim());
            if (templateId!=null) {
                printerTemplateIds.put(printerName, templateId);
            } else {
                log.error("Printer name {} does not have template id in pmb.properties file", printerName);
                throw new IOException("Printer does not have template id in pmb.properties file");
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
