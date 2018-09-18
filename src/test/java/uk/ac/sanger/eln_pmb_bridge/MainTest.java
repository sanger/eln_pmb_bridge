package uk.ac.sanger.eln_pmb_bridge;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author hc6
 */
public class MainTest {

    @Test
    public void testMainNoEnvironmentModeInMainArgs() {
        try {
            Main.main(new String[] {});
        } catch (Exception e) {
            assertEquals(e.getMessage().trim(), "No environment mode was provided in Main args.");
        }
    }

    @Test
    public void testMainUnknownEnvironmentModeInMainArgs() {
        try {
            Main.main(new String[] {"env=abc"});
        } catch (Exception e) {
            assertEquals(e.getMessage().trim(), "Unknown environment mode was provided.");
        }
    }

}
