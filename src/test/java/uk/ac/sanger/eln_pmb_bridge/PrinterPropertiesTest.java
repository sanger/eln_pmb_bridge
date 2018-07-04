package uk.ac.sanger.eln_pmb_bridge;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.testng.AssertJUnit.assertTrue;

/**
 * Created by hc6 on 04/07/2018.
 */
public class PrinterPropertiesTest {

    @BeforeClass
    public void setup() throws IOException {
        ELNPMBProperties.setProperties("./test_properties_folder/eln_pmb.properties");
    }

    @Test
    public void TestSetPropertiesSuccessful() throws IOException {
        PrinterProperties.setProperties("./test_properties_folder/printer.properties");

        assertTrue(PrinterProperties.getPrinterList().size() == 2);
        assertTrue(PrinterProperties.getPrinterTemplateIdList().containsKey("123456"));
    }

    @Test void TestGetPrinterList() throws IOException {
        PrinterProperties.setProperties("./test_properties_folder/printer.properties");

        assertTrue(PrinterProperties.getPrinterList().getClass().equals(ArrayList.class));
        assertTrue(PrinterProperties.getTemplateIdForPrinter("654321").equals(2));
    }

    @Test void TestGetTemplateIdForPrinter() throws IOException {
        PrinterProperties.setProperties("./test_properties_folder/printer.properties");

        assertTrue(PrinterProperties.getTemplateIdForPrinter("123456").equals(1));
        assertTrue(PrinterProperties.getTemplateIdForPrinter("654321").equals(2));
    }
}
