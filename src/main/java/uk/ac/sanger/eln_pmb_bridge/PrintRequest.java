package uk.ac.sanger.eln_pmb_bridge;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.*;

/**
 * A request object to print labels via PrintMyBarcode
 * @author hc6
 */
public class PrintRequest {
    private String printerName;
    private List<Label> labels;
    private int numOfCopies;

    @JsonCreator
    public PrintRequest(
            @JsonProperty("printerName") String printerName,
            @JsonProperty("labels") List<Label> labels,
            @JsonProperty("numOfCopies") Integer numOfCopies) {
        this.printerName = printerName;
        this.labels = labels;
        this.numOfCopies = numOfCopies;
    }

    public List<Label> getLabels() {
        return labels;
    }

    public String getPrinterName() {
        return this.printerName;
    }

    public int getNumOfCopies() { return this.numOfCopies; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PrintRequest that = (PrintRequest) o;
        return (this.printerName.equals(that.printerName)
                && this.labels.equals(that.labels)
                && Integer.valueOf(this.numOfCopies).equals(that.numOfCopies));
    }

    @Override
    public String toString() {
        return "PrintRequest{" +
                "printerName='" + printerName + '\'' +
                ", labels=" + labels +
                ", numOfCopies=" + numOfCopies +
                '}';
    }

    public int length() {
        return this.labels.size();
    }

    public static class Label {
        private Map<String, String> fields;

        @JsonCreator
        public Label(
                @JsonProperty("fields") Map<String, String> fields) {
            this.fields = fields;
        }

        public Map<String, String> getFields() {
            return this.fields;
        }

        public String getField(String key) {
            return this.fields.get(key);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Label that = (Label) o;
            return (this.fields.equals(that.fields));
        }

        @Override
        public String toString() {
            return "Label{" + "fields=" + fields + '}';
        }
    }

}
