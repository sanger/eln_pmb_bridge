package uk.ac.sanger.eln_pmb_bridge;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * @author hc6
 */

public class EmailServiceTest {

    @BeforeMethod
    public void setUp() throws Exception {
        MailProperties.setProperties("./test_properties_folder/mail.properties");
    }

    @Test
    public void TestGetMailPropertiesSuccessful() throws Exception {
        String toAddress = MailProperties.getMailTo().trim();
        assertEquals(toAddress, "user@here.com");
    }

    @Test
    public void TestSetEmailServiceEnvModeNull() {
        try {
            EmailService.setService(null);
            fail("An exception should have been thrown.");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage().trim(), "No environment mode was provided for Email Service.");
        }
    }

    @Test
    public void TestSetEmailServiceSuccessful() {
        EmailService.setService(Main.EnvironmentMode.TEST);
        assertNotNull(EmailService.getService());
    }

    @Test
    public void TestSendStartUpEmail() throws Exception {
        EmailService.setService(Main.EnvironmentMode.TEST);
        EmailService emailService = mock(EmailService.class);

        doCallRealMethod().when(emailService).sendStartUpEmail();
        doCallRealMethod().when(emailService).sendEmail(anyString(), anyString());

        emailService.sendStartUpEmail();

        verify(emailService, times(1)).sendEmail(anyString(), anyString());
        verify(emailService, times(1)).send(any());

    }

    @Test
    public void TestSendErrorEmail() throws Exception {
        EmailService.setService(Main.EnvironmentMode.TEST);
        EmailService emailService = mock(EmailService.class);

        doCallRealMethod().when(emailService).sendErrorEmail(anyString(), any());
        doCallRealMethod().when(emailService).sendEmail(anyString(), anyString());

        emailService.sendErrorEmail("subject error", new Exception());

        verify(emailService, times(1)).sendEmail(anyString(), anyString());
        verify(emailService, times(1)).send(any());
    }

}