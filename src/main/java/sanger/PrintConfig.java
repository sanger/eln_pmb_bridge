package sanger;

import java.util.EnumMap;
import java.util.Map;

/**
 * Created by hc6 on 18/11/2016.
 */
public class PrintConfig {
    private static PrintConfig instance;
    private String baseLocation;
    private Map<PrinterLabelType, Integer> templateIds;

    public PrintConfig(String baseLocation, Map<PrinterLabelType, Integer> templateIds) {
        this.baseLocation = baseLocation;
        this.templateIds = templateIds;
    }

    public static PrintConfig getInstance() {
        return instance;
    }

    public static void setInstance(PrintConfig instance) {
        PrintConfig.instance = instance;
    }

    public String getLocalLocation() {
        return this.baseLocation;
    }

    public Map<PrinterLabelType, Integer> getTemplateIds() {
        return this.templateIds;
    }

    public static void loadConfig() {
//        location and templateIds will come from properties file
        String location = "http://localhost:3000/v1";

        Map<PrinterLabelType, Integer> templateIds = new EnumMap<>(PrinterLabelType.class);
        templateIds.put(PrinterLabelType.Plate, 6);
        templateIds.put(PrinterLabelType.Tube, 0);
        templateIds.put(PrinterLabelType.Branded, 0);

        PrintConfig printConfig = new PrintConfig(location, templateIds);
        setInstance(printConfig);
    }

    public Integer getTemplateIdForPrinter(String printer) {
        return getTemplateId(getLabelType(printer));
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

    private Integer getTemplateId(PrinterLabelType labelType) {
        return this.templateIds.get(labelType);
    }

}
