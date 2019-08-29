package uk.ac.sanger.eln_pmb_bridge;

import org.testng.annotations.Test;
import uk.ac.sanger.eln_pmb_bridge.PrinterConfig.Entry;
import uk.ac.sanger.eln_pmb_bridge.PrinterConfig.Service;

import java.io.IOException;

import static org.testng.Assert.*;

/**
 * @author dr6
 */
public class PrinterConfigTest {
    @Test
    public void testPrinterConfig() throws IOException {
        PrinterConfig.initialise("./test_properties_folder/printer.properties");
        PrinterConfig pc = PrinterConfig.getInstance();

        assertTrue(pc.hasPrinterConfig("123456"));
        assertEquals(pc.getPrinterConfig("123456"), new Entry(Service.PMB, LabelType.thin));
        assertTrue(pc.hasPrinterConfig("654321"));
        assertEquals(pc.getPrinterConfig("654321"), new Entry(Service.PMB, LabelType.fat));
        assertTrue(pc.hasPrinterConfig("abcdef"));
        assertEquals(pc.getPrinterConfig("abcdef"), new Entry(Service.SPRINT, LabelType.thin));

        assertFalse(pc.hasPrinterConfig("xyz"));
        try {
            pc.getPrinterConfig("xyz");
            fail("expected an exception");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("xyz"));
        }
    }
}
