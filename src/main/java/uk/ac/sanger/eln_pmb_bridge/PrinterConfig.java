package uk.ac.sanger.eln_pmb_bridge;

import java.io.IOException;
import java.util.*;

/**
 * @author dr6
 */
public class PrinterConfig {
    private static PrinterConfig instance;

    enum Service {
        PMB, SPRINT
    }

    public static class Entry {
        public final Service service;
        public final LabelType labelType;

        public Entry(Service service, LabelType labelType) {
            this.service = service;
            this.labelType = labelType;
        }

        @Override
        public int hashCode() {
            return service.hashCode()*7+labelType.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this==obj) return true;
            if (obj==null || obj.getClass() != this.getClass()) return false;
            Entry that = (Entry) obj;
            return (this.service==that.service && this.labelType==that.labelType);
        }

        @Override
        public String toString() {
            return String.format("[%s,%s]", service, labelType);
        }
    }

    private Map<String, Entry> printers;

    public PrinterConfig(Map<String, Entry> printers) {
        this.printers = printers;
    }

    public Entry getPrinterConfig(String printerName) {
        Entry entry = printers.get(printerName.toLowerCase());
        if (entry==null) {
            throw new IllegalArgumentException(ErrorType.UNKNOWN_PRINTER_NAME.getMessage() + printerName);
        }
        return entry;
    }

    public boolean hasPrinterConfig(String printerName) {
        return (printers.get(printerName.toLowerCase()) != null);
    }

    public static PrinterConfig getInstance() {
        return PrinterConfig.instance;
    }

    public static void initialise(String configFilename) throws IOException {
        instance = load(configFilename);
    }

    private static PrinterConfig load(String configFilename) throws IOException {
        Properties properties = PropertiesFileReader.loadFile(configFilename);
        return load(properties);
    }

    private static PrinterConfig load(Properties properties) throws IOException {
        Map<String, Entry> printerMap = new HashMap<>();
        for (Map.Entry<?, ?> mapEntry : properties.entrySet()) {
            if (mapEntry.getKey() instanceof String && mapEntry.getValue() instanceof String) {
                String key = ((String) mapEntry.getKey()).trim().toLowerCase();
                String value = ((String) mapEntry.getValue());
                Entry entry = parseEntry(value);
                printerMap.put(key, entry);
            }
        }
        return new PrinterConfig(printerMap);
    }

    private static Entry parseEntry(String value) throws IOException {
        String[] parts = value.split("/");
        if (parts.length!=2) {
            throw new IOException(ErrorType.INVALID_PRINTER_ENTRY.getMessage()+value);
        }
        Service service = Service.valueOf(parts[0].trim().toUpperCase());
        LabelType labelType = LabelType.valueOf(parts[1].trim().toLowerCase());
        return new Entry(service, labelType);
    }

}
