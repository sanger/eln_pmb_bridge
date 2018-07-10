package uk.ac.sanger.eln_pmb_bridge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * Service for sending emails implementing the Singleton design pattern
 * @author hc6
 */
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private static EmailService service;

    public static EmailService getService() {
        if (service == null) {
            service = new EmailService();
        }
        return service;
    }

    protected void send(Message message) throws MessagingException {
        Transport.send(message);
    }

    protected void sendEmail(String subject, String text) throws Exception {

        Properties mailProperties = MailProperties.getProperties();
        Session session = Session.getInstance(mailProperties);
        MimeMessage message = new MimeMessage(session);
        String toAddress = MailProperties.getMailTo();

        try {
            message.addRecipients(Message.RecipientType.TO, toAddress);
            message.setSubject(subject);
            message.setContent(text, "text/html; charset=utf-8");
            send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            log.error(ErrorType.FAILED_EMAIL + String.format("Subject:  %s", subject));
            throw new Exception();
        }

        log.info(String.format("Successfully sent email (subject: \"%s\") to \"%s\"", subject, toAddress));
    }

    protected void sendStartUpEmail() throws Exception {
        String currentTime = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        String message = String.format("Starting up ELN PMB Bridge Service at %s", currentTime);
        sendEmail("Starting up ELN PMB Bridge Service", message);
    }

    protected void sendErrorEmail(String subject, Exception e) throws Exception {
        String message = ErrorType.MOVE_TO_ERROR_FOLDER.getMessage() + String.format("Exception: %s", e);
        sendEmail(subject, message);
    }

}
