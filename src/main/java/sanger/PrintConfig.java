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

    private static PrintConfig instance;
    private String pmbURL;
    private Map<PrinterLabelType, Integer> templateIds;

    public PrintConfig(String pmbURL, Map<PrinterLabelType, Integer> templateIds) {
        this.pmbURL = pmbURL;
        this.templateIds = templateIds;
    }

    public static PrintConfig loadConfig() {
        PrintConfig p;
        try {
            p = loadProperties();
        } catch (Exception e) {
            p = null;
            log.error("Failed to load print config", e);
        }
        setInstance(p);
        log.info("Set up print configuration");
        return p;
    }

    public static PrintConfig loadProperties() throws IOException {
        FileManager fileManager = new FileManager();
        Properties properties = fileManager.readPropertiesFile("pmb.properties");

        String pmbURL = properties.getProperty("pmb_url", "");
        if (pmbURL.isEmpty()) {
            throw new IOException("No host supplied in pmb.properties");
        }
        Map<PrinterLabelType, Integer> templateIds = new EnumMap<>(PrinterLabelType.class);
        for (PrinterLabelType labelType : PrinterLabelType.values()) {
            String templateIdString = properties.getProperty(labelType.name().toLowerCase(), "").trim();
            if (!templateIdString.isEmpty()) {
                Integer templateId = Integer.valueOf(templateIdString.trim());
                templateIds.put(labelType, templateId);
            }
        }
        return new PrintConfig(pmbURL, templateIds);
    }

    public static PrintConfig getInstance() {
        return instance;
    }

    public static void setInstance(PrintConfig instance) {
        PrintConfig.instance = instance;
    }

    public String getPmbURL() {
        return this.pmbURL;
    }

    public Map<PrinterLabelType, Integer> getTemplateIds() {
        return this.templateIds;
    }

    public Integer getTemplateIdForPrinter(String printer) {
        return getTemplateId(getLabelType(printer));
    }

    private Integer getTemplateId(PrinterLabelType labelType) {
        return this.templateIds.get(labelType);
    }

    private PrinterLabelType getLabelType(String printerName) {
        if (printerName!=null) {
            switch (printerName) {
                case "d304bc" : return PrinterLabelType.Plate;
                case "e367bc" : return PrinterLabelType.Tube;
            }
        }
        return null;
    }

}
