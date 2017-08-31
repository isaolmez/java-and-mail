package com.isa.java.mail.search;

import com.isa.java.mail.util.ApplicationProperties.GmailImapProperties;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Properties;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.RecipientStringTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SentDateTerm;

public class SearchMails {

    private static final String PROTOCOL = "imap";
    private static final String FOLDER = "INBOX";
    private static final GmailImapProperties IMAP_PROPERTIES = new GmailImapProperties();

    public static void main(String[] args) {
        check(IMAP_PROPERTIES.getHost(), IMAP_PROPERTIES.getPort(), PROTOCOL,
                IMAP_PROPERTIES.getUsername(), IMAP_PROPERTIES.getPassword());
    }

    public static void check(String host, int port, String storeType, String user,
            String password) {
        try {
            // Get session
            Properties properties = new Properties();
            properties.setProperty("mail.imap.ssl.enable", "true");
            Session emailSession = Session.getInstance(properties);

            // Get store
            Store store = emailSession.getStore(storeType);
            store.connect(host, port, user, password);

            // Get folder
            Folder emailFolder = store.getFolder(FOLDER);
            emailFolder.open(Folder.READ_ONLY);

            // Message count
            int messageCount = emailFolder.getMessageCount();
            System.out.println("Message count: " + messageCount);
            int newMessageCount = emailFolder.getNewMessageCount();
            System.out.println("New message count: " + newMessageCount);

            // Search the messages
            Date date = Date.from(LocalDateTime.now().minusDays(1).toInstant(ZoneOffset.UTC));
            SearchTerm searchTerm = new DateAndToSearchTerm(date, user);
            Message[] messages = emailFolder.search(searchTerm);
            System.out.println("Message count: " + messages.length);

            for (int i = 0, n = 2; i < n; i++) {
                Message message = messages[i];
                System.out.println("---------------------------------");
                System.out.println("Email Number " + (i + 1));
                System.out.println("Subject: " + message.getSubject());
                System.out.println("From: " + message.getFrom()[0]);
                System.out.println("Text: " + message.getContent().toString());
                message.writeTo(System.out);
            }

            // Close the store and folder, don't expunge
            emailFolder.close(false);
            store.close();

        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class DateAndToSearchTerm extends SearchTerm {

        private SentDateTerm sentDateTerm;

        private RecipientStringTerm recipientStringTerm;

        public DateAndToSearchTerm(Date afterDate, String to) {
            this.sentDateTerm = new SentDateTerm(ComparisonTerm.GE, afterDate);
            this.recipientStringTerm = new RecipientStringTerm(RecipientType.TO, to);
        }

        @Override
        public boolean match(Message message) {
            if (sentDateTerm.match(message) && recipientStringTerm.match(message)) {
                return true;
            }

            return false;
        }
    }
}