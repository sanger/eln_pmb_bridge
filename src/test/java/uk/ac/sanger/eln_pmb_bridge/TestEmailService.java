package uk.ac.sanger.eln_pmb_bridge;

import org.powermock.api.mockito.PowerMockito;
import org.testng.annotations.Test;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import static org.mockito.Matchers.any;
import static org.testng.Assert.assertEquals;

/**
 * @author hc6
 */
public class TestEmailService {
    protected Properties mailProperties;

    @Test
    public void ThenTheErrorEmailShouldBeSuccessful() throws Exception {
        mailProperties = new Properties();
        mailProperties.setProperty("host", "");
        mailProperties.setProperty("port", "");
        mailProperties.setProperty("to", "hc6@sanger.ac.uk");

        EmailService emailService = PowerMockito.spy(new EmailService(mailProperties));
        PowerMockito.doNothing().when(emailService, "sendEmail", any());

        emailService.sendEmail("Email text");

        Session session = Session.getInstance(mailProperties);
        MimeMessage message = new MimeMessage(session);
        message.setSubject("Email subject");
        message.setContent("Email content", "text/html; charset=utf-8");

        assertEquals(message.getSession().getProperties(), mailProperties);
        assertEquals(message.getSubject(), "Email subject");
        assertEquals(message.getContent(), "Email content");
        assertEquals(message.getDataHandler().getContentType(), "text/html; charset=utf-8");
        assertEquals(mailProperties.getProperty("to"), "hc6@sanger.ac.uk");
    }
}
