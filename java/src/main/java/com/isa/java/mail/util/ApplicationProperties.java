package com.isa.java.mail.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApplicationProperties {

    private static Properties properties;

    static {
        properties = new Properties();
        InputStream propertiesStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("application.properties");
        try {
            properties.load(propertiesStream);
        } catch (IOException e) {
            throw new CannotLoadPropertiesException("Could not load properties file", e);
        }
    }

    public static class GmailProperties {

        public String getUsername() {
            return properties.getProperty("mail.gmail.username");
        }

        public String getPassword() {
            return properties.getProperty("mail.gmail.password");
        }
    }

    public static class GmailImapProperties extends GmailProperties {

        public String getHost() {
            return properties.getProperty("mail.gmail.imap.host");
        }

        public int getPort() {
            return Integer.parseInt(properties.getProperty("mail.gmail.imap.port"));
        }

        public String getPortAsString() {
            return properties.getProperty("mail.gmail.imap.port");
        }

        public String getTimeOut() {
            return properties.getProperty("mail.gmail.imap.timeout");
        }
    }

    public static class GmailPopProperties extends GmailProperties {

        public String getHost() {
            return properties.getProperty("mail.gmail.pop.host");
        }

        public int getPort() {
            return Integer.parseInt(properties.getProperty("mail.gmail.pop.port"));
        }
    }

    public static class GmailSmtpProperties extends GmailProperties {

        public String getHost() {
            return properties.getProperty("mail.gmail.smtp.host");
        }

        public int getTlsPort() {
            return Integer.parseInt(properties.getProperty("mail.gmail.smtp.tlsPort"));
        }

        public int getSslPort() {
            return Integer.parseInt(properties.getProperty("mail.gmail.smtp.sslPort"));
        }
    }

    static class CannotLoadPropertiesException extends RuntimeException {

        CannotLoadPropertiesException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
