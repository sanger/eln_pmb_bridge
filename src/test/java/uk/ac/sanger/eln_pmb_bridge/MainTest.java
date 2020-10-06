package uk.ac.sanger.eln_pmb_bridge;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * @author hc6
 */
public class MainTest {

    @Test
    public void testMainNoEnvironmentModeInMainArgs() {
        try {
            Main.main(new String[] {});
            fail("An exception should have been thrown.");
        } catch (Exception e) {
            assertEquals(e.getMessage().trim(), "No environment mode was provided in Main args.");
        }
    }

    @Test
    public void testMainUnknownEnvironmentModeInMainArgs() {
        try {
            Main.main(new String[] {"env=abc"});
            fail("An exception should have been thrown.");
        } catch (Exception e) {
            assertEquals(e.getMessage().trim(), "Unknown environment mode was provided.");
        }
    }

}
