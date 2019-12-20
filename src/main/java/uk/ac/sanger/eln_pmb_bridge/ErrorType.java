package uk.ac.sanger.eln_pmb_bridge;

/**
 * @author hc6
 */
public enum ErrorType {

    ELN_PMB_SUBJECT("ELN PMB Bridge. "),
    FATAL("This is a fatal error. "),
    RECOVERABLE("This is a recoverable error. "),
    MISSING_PMB_URL("Print My Barcode's url is missing from the properties folder. "),
    MISSING_DIR_PATHS("Directory paths are missing from the eln_pmb.properties"),
    MISSING_PROP_FILE("Property file path is missing. "),
    UNKNOWN_PRINTER_NAME("The printer name does not exist. "),
    NO_TEMPLATE_ID("There is at least one template id missing in eln_pmb.properties. "),
    NO_LABELS("There are no labels to be printed in the request. "),
    NO_PRINTER_NAME("There is no printer name in the request. "),
    NO_NUMBER_OF_COPIES("There is no number of copies specified in the request. "),
    FAILED_EMAIL("Failed to send email. "),
    FAILED_FOLDER_CREATION("Failed to create new folder. "),
    MOVE_TO_ERROR_FOLDER("Moving file to error folder. "),
    EMPTY_PROP_FILE("The property file is empty. "),
    NO_EMAIL_SERVICE("There is no Email Service. "),
    NO_ENV_MODE_FOR_EMAIL_SERVICE("No environment mode was provided for Email Service. "),
    NO_ENV_MODE_IN_MAIN_ARGS("No environment mode was provided in Main args. "),
    UNKNOWN_ENV_MODE("Unknown environment mode was provided. "),
    INVALID_PRINTER_ENTRY("Cannot parse value in printer config: "),
    UNKNOWN_PRINT_SERVICE("Unknown print service: "),
    MISSING_TEMPLATE("No suitable template found for label type "),
    MISSING_SPRINT_URL("SPrint url missing from config. "),
    WRONG_ROW_LENGTH("A row in the print request had a different number of elements to the header. "),
    ;

    private final String message;

    ErrorType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}