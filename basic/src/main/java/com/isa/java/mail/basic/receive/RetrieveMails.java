package com.isa.java.mail.basic.receive;

import com.isa.java.mail.basic.util.ApplicationProperties.GmailPopProperties;
import java.util.Properties;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;

public class RetrieveMails {

    private static final String PROTOCOL = "pop3";
    private static final String FOLDER = "INBOX";
    private static final GmailPopProperties POP_PROPERTIES = new GmailPopProperties();

    public static void main(String[] args) {
        check(POP_PROPERTIES.getHost(), POP_PROPERTIES.getPort(), PROTOCOL,
                POP_PROPERTIES.getUsername(), POP_PROPERTIES.getPassword());
    }

    public static void check(String host, int port, String storeType, String user,
            String password) {
        try {
            Properties props = new Properties();
            props.setProperty("mail.pop3.ssl.enable", "true");
            Session emailSession = Session.getInstance(props);

            Store store = emailSession.getStore(storeType);
            store.connect(host, port, user, password);

            Folder emailFolder = store.getFolder(FOLDER);
            emailFolder.open(Folder.READ_ONLY);

            // Message count
            int messageCount = emailFolder.getMessageCount();
            System.out.println("Message count: " + messageCount);
            int newMessageCount = emailFolder.getNewMessageCount();
            System.out.println("New message count: " + newMessageCount);

            // Retrieve the messages
            Message[] messages = emailFolder.getMessages();
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}