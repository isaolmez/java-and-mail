package com.isa.java.mail.spring.runner;

import com.isa.java.mail.spring.service.EmailService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class EmailSender implements ApplicationRunner {

    private final EmailService emailService;

    @Autowired
    public EmailSender(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        final String defaultEmail = "java.mail.spring@mailinator.com";
        final String defaultSubject = "Test";
        final String defaultMessage = "Message";
        if (args != null) {
            List<String> toValues = args.getOptionValues("to");
            String to = toValues == null || toValues.isEmpty() ? defaultEmail : toValues.get(0);
            List<String> subjectValues = args.getOptionValues("subject");
            String subject = subjectValues == null || subjectValues.isEmpty() ? defaultSubject : subjectValues.get(0);
            List<String> messageValues = args.getOptionValues("message");
            String message = messageValues == null || messageValues.isEmpty() ? defaultMessage : messageValues.get(0);
            emailService.helloWorld(to, subject, message);
        } else {
            emailService.helloWorld(defaultEmail, defaultSubject, defaultMessage);
        }
    }
}
