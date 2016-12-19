package uk.ac.sanger.eln_pmb_bridge;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Service for sending emails to beagledev if there is an error with the WatchService
 * @author hc6
 */
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(FileManager.class);
    private Properties mailProperties;

    public EmailService(Properties mailProperties) {
        this.mailProperties = mailProperties;
    }

    public void sendEmail(String text) throws MessagingException, javax.mail.MessagingException {
        Session session = Session.getInstance(mailProperties);
        MimeMessage message = new MimeMessage(session);
        String toAddress = mailProperties.getProperty("to", "").trim();

        message.addRecipients(Message.RecipientType.TO, toAddress);
        message.setSubject("ELN PMB Bridge - error");
        message.setContent(text, "text/html; charset=utf-8");

        Transport.send(message);
        log.info(String.format("Successfully sent error email to \"%s\"", toAddress));
    }

}
