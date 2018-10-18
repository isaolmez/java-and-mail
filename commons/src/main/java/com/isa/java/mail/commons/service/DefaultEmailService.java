package com.isa.java.mail.commons.service;

import java.util.Arrays;
import javax.mail.internet.InternetAddress;
import lombok.SneakyThrows;
import org.apache.commons.mail.SimpleEmail;

public class DefaultEmailService implements EmailService {

    private final EmailProvider<SimpleEmail> emailProvider;

    public DefaultEmailService(EmailProvider<SimpleEmail> emailProvider) {
        this.emailProvider = emailProvider;
    }

    @SneakyThrows
    @Override
    public void helloWorld() {
        SimpleEmail simpleEmail = emailProvider.newEmailWithExplicitSession();
        simpleEmail.setSubject("Hello World!");
        simpleEmail.setMsg("This is a Hello World example ");
        InternetAddress address = convertToInternetAddress("java.mail.commons@mailinator.com");
        simpleEmail.setTo(Arrays.asList(address));
        simpleEmail.send();
    }

    @SneakyThrows
    protected InternetAddress convertToInternetAddress(String recipient) {
        return new InternetAddress(recipient.trim());
    }
}
