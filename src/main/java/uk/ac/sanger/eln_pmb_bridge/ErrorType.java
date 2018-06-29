package uk.ac.sanger.eln_pmb_bridge;

/**
 * Created by hc6 on 29/06/2018.
 */
public enum ErrorType {

    FATAL("This is a fatal error."),
    RECOVERABLE("This is a recoverable error."),
    MISSING_PMB_URL("Print My Barcode's url is missing from the properties folder."),
    UNKNOWN_PRINTER_NAME("The printer name does not exist"),
    NO_PRINTERS("The list of printers is empty in the properties folder."),
    NO_TEMPLATE_IDS("The list of PMB template ids is empty"),
    NO_LABELS("There are no labels to be printed in the request"),
    NO_PRINTER_NAME("There is no printer name in the request");


    private final String message;

    ErrorType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}