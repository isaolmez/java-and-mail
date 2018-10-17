package com.isa.java.mail.basic.send;

import com.isa.java.mail.basic.util.ApplicationProperties.GmailSmtpProperties;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMail {

    private static final GmailSmtpProperties SMTP_PROPERTIES = new GmailSmtpProperties();

    public static void main(String[] args) {
        check(SMTP_PROPERTIES.getHost(), SMTP_PROPERTIES.getTlsPort(), SMTP_PROPERTIES.getUsername(),
                SMTP_PROPERTIES.getPassword());
    }

    public static void check(String host, int port, String user, String password) {
        // Get session with properties
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.starttls.enable", "true");
        Session session = Session.getInstance(properties);

        try {
            // Construct the message
            Message message = new MimeMessage(session);
            message.setText(String.format("Javax mail test %s", System.currentTimeMillis()));
            Address address = new InternetAddress(user, "John Doe");
            message.setFrom(address);
            message.setRecipient(RecipientType.TO, address);
            message.setSubject("Test subject");

            Transport transport = session.getTransport();
            transport.connect(host, port, user, password);
            transport.sendMessage(message, message.getAllRecipients());
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}