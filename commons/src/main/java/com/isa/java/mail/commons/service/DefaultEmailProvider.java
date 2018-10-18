package com.isa.java.mail.commons.service;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Session;
import lombok.SneakyThrows;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.SimpleEmail;

public class DefaultEmailProvider implements EmailProvider<SimpleEmail> {

    private final String username = "";
    private final String password = "";

    @SneakyThrows
    @Override
    public SimpleEmail newEmail() {
        SimpleEmail email = new SimpleEmail();
        email.setFrom("java.mail@mailinator.com", "Test");
        Authenticator authenticator = new DefaultAuthenticator(username, password);
        email.setAuthenticator(authenticator);
        email.setStartTLSEnabled(true);
        email.setStartTLSRequired(true);
        email.setHostName("smtp.gmail.com");
        email.setSmtpPort(587);
        email.setSslSmtpPort("587");
        return email;
    }

    @SneakyThrows
    @Override
    public SimpleEmail newEmailWithExplicitSession() {
        SimpleEmail email = new SimpleEmail();
        email.setFrom("java.mail.commons@mailinator.com", "Test");
        Authenticator authenticator = new DefaultAuthenticator(username, password);
        email.setMailSession(newSession(authenticator));
        return email;
    }

    private Session newSession(Authenticator authenticator) {
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.host", "smtp.gmail.com");
        properties.setProperty("mail.smtp.port", "587");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.auth", "true");

        return Session.getInstance(properties, authenticator);
    }
}
