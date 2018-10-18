package com.isa.java.mail.commons.service;

import org.apache.commons.mail.SimpleEmail;

public class Client {

    public static void main(String[] args) {
        EmailProvider<SimpleEmail> emailProvider = new DefaultEmailProvider();
        EmailService emailService = new DefaultEmailService(emailProvider);
        emailService.helloWorld();
    }
}
