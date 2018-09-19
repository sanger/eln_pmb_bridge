package uk.ac.sanger.eln_pmb_bridge;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Properties;

import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

/**
 * @author hc6
 */
public class PrinterPropertiesTest {

    @BeforeClass
    public void setup() throws IOException {
        ELNPMBProperties.setProperties("./test_properties_folder/eln_pmb.properties");
    }

    @Test
    public void TestSetPropertiesSuccessful() throws IOException {
        PrinterProperties.setProperties("./test_properties_folder/printer.properties");

        assertTrue(ELNPMBProperties.getProperties().getClass().equals(Properties.class));
        assertTrue(PrinterProperties.getPrinterList().size() == 2);
        assertTrue(PrinterProperties.getPrinterTemplateIdList().containsKey("123456"));
        assertTrue(PrinterProperties.getTemplateIdForPrinter("123456").equals(1));
        assertTrue(PrinterProperties.getTemplateIdForPrinter("654321").equals(2));
    }

    @Test
    public void TestSetPropertiesMissingPrinterList() throws IOException {
        try {
            PrinterProperties.setProperties("./test_properties_folder/empty.properties");
        } catch (IOException e) {
            assertEquals(e.getMessage().trim(), "The property file is empty.");
        }
    }

    @Test
    public void TestSetPropertiesMissingPrinterSize() throws IOException {
        try {
            PrinterProperties.setProperties("./test_properties_folder/printer_missing_size.properties");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage().trim(), "The is no printer size for this printer in printer.properties. 123456");
        }
    }

}
