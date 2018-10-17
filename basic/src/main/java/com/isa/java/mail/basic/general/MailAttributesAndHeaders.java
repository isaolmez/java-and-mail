package com.isa.java.mail.basic.general;

import com.isa.java.mail.basic.util.ApplicationProperties.GmailPopProperties;
import java.util.Enumeration;
import java.util.Properties;
import javax.mail.Folder;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;

public class MailAttributesAndHeaders {

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
            //Get session
            Properties properties = new Properties();
            properties.setProperty("mail.pop3.ssl.enable", "true");
            Session session = Session.getInstance(properties);

            // Get store
            Store store = session.getStore(storeType);
            store.connect(host, port, user, password);

            // Get folder
            Folder inbox = store.getFolder(FOLDER);
            inbox.open(Folder.READ_ONLY);

            // Message count
            int messageCount = inbox.getMessageCount();
            System.out.println("Message count: " + messageCount);
            int newMessageCount = inbox.getNewMessageCount();
            System.out.println("New message count: " + newMessageCount);

            Message[] messages = inbox.getMessages(1, 2);
            System.out.println("Message count: " + messages.length);

            for (int i = 0, n = 2; i < n; i++) {
                Message message = messages[i];

                System.out.println("---------------------------------");
                System.out.println("Email Number " + (i + 1));
                System.out.println("Subject: " + message.getSubject());
                System.out.println("From: " + message.getFrom()[0]);
                System.out.println("Text: " + message.getContent().toString());

                // Attributes
                System.out.println("ATTRIBUTES ---------------------------------");
                System.out.println("This message is approximately "
                        + messages[i].getSize() + " bytes long.");
                System.out.println("This message has approximately "
                        + messages[i].getLineCount() + " lines.");
                String disposition = messages[i].getDisposition();
                if (disposition == null) {
                    // do nothing
                } else if (disposition.equals(Part.INLINE)) {
                    System.out.println("This part should be displayed inline");
                } else if (disposition.equals(Part.ATTACHMENT)) {
                    System.out.println("This part is an attachment");
                    String fileName = messages[i].getFileName();
                    if (fileName != null) {
                        System.out.println("The file name of this attachment is "
                                + fileName);
                    }
                }
                String description = messages[i].getDescription();
                if (description != null) {
                    System.out.println("The description of this message is "
                            + description);
                }

                // Headers
                System.out.println("HEADERS ---------------------------------");
                Enumeration<Header> headers = messages[i].getAllHeaders();
                while (headers.hasMoreElements()) {
                    Header h = headers.nextElement();
                    System.out.println(h.getName() + ": " + h.getValue());
                }
            }

            inbox.close(false);
            store.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}