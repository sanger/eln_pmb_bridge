package sanger;

import java.util.List;
import java.util.Map;

/**
 * @author hc6
 */
public class PrintRequest {
    private String printerName;
    private List<Label> labels;

    public PrintRequest(String printerName, List<Label> labels) {
        this.printerName = printerName;
        this.labels = labels;
    }

    public List<Label> getLabels() {
        return labels;
    }

    public String getPrinterName() {
        return this.printerName;
    }

    @Override
    public String toString() {
        return "PrintRequest{" +
                "printerName='" + printerName + '\'' +
                ", labels=" + labels +
                '}';
    }


    public static class Label {
        private Map<String, String> fields;

        public Label(Map<String, String> fields) {
            this.fields = fields;
        }

//        eg {cell_line=zogh, barcode=20000000001}
        public Map<String, String> getFields() {
            return this.fields;
        }

        @Override
        public String toString() {
            return "Label{" + "fields=" + fields + '}';
        }
    }

}
