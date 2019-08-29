package uk.ac.sanger.eln_pmb_bridge;

import org.testng.annotations.Test;

import java.util.*;

import static org.testng.Assert.assertEquals;

/**
 * @author dr6
 */
public class StringTemplateTest {
    @Test
    public void testStringTemplate() {
        StringTemplate template = new StringTemplate("#Alpha##Beta#--#Gamma##Alpha#", "#", "#");
        assertEquals(template.substitute(map("Alpha", "*A*", "Beta", "*B*")),
                "*A**B*--*A*");
        assertEquals(template.substitute(map("Gamma", "*G*")), "--*G*");
    }

    private static Map<String, String> map(String... elements) {
        if (elements.length==0) {
            return Collections.emptyMap();
        }
        if (elements.length==2) {
            return Collections.singletonMap(elements[0], elements[1]);
        }
        Map<String, String> map = new HashMap<>(elements.length/2);
        for (int i = 0; i < elements.length; i += 2) {
            map.put(elements[i], elements[i+1]);
        }
        return map;
    }
}
