package com.isa.java.mail.general;

import com.isa.java.mail.util.ApplicationProperties.GmailPopProperties;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MailContent {

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

            Properties properties = new Properties();
            properties.setProperty("mail.pop3.ssl.enable", "true");
            Session session = Session.getDefaultInstance(properties);

            Store store = session.getStore(storeType);
            store.connect(host, port, user, password);

            Folder folder = store.getFolder(FOLDER);
            folder.open(Folder.READ_ONLY);

            // Message count
            int messageCount = folder.getMessageCount();
            System.out.println("Message count: " + messageCount);
            int newMessageCount = folder.getNewMessageCount();
            System.out.println("New message count: " + newMessageCount);

            Message[] messages = folder.getMessages(1, 2);
            System.out.println("Message count: " + messages.length);

            for (int i = 0, n = 2; i < n; i++) {
                Message message = messages[i];
                System.out.println("---------------------------------");
                System.out.println("Email Number " + (i + 1));
                System.out.println("Subject: " + message.getSubject());
                System.out.println("From: " + message.getFrom()[0]);
                System.out.println("Text: " + message.getContent().toString());

                // Parts
                System.out.println("PARTS ---------------------------------");
                Object body = messages[i].getContent();
                if (body instanceof Multipart) {
                    processMultipart((Multipart) body);
                } else { // ordinary message
                    processPart(messages[i]);
                }
            }

            folder.close(false);
            store.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void processMultipart(Multipart mp)
            throws MessagingException {
        for (int i = 0; i < mp.getCount(); i++) {
            processPart(mp.getBodyPart(i));
        }
    }

    public static void processPart(Part p) {
        try {
            String fileName = p.getFileName();
            String disposition = p.getDisposition();
            String contentType = p.getContentType();
            if (contentType.toLowerCase().startsWith("multipart/")) {
                processMultipart((Multipart) p.getContent());
            } else if (fileName == null
                    && (Part.ATTACHMENT.equalsIgnoreCase(disposition)
                    || !contentType.equalsIgnoreCase("text/plain"))) {
                // pick a random file name.
                fileName = File.createTempFile("attachment", ".txt").getName();
            }
            if (fileName == null) { // likely inline
                p.writeTo(System.out);
            } else {
                File f = new File(fileName);
                // find a file that does not yet exist
                for (int i = 1; f.exists(); i++) {
                    String newName = fileName + " " + i;
                    f = new File(newName);
                }
                try (
                        OutputStream out = new BufferedOutputStream(new FileOutputStream(f));
                        InputStream in = new BufferedInputStream(p.getInputStream())) {
                    // We can't just use p.writeTo() here because it doesn't
                    // decode the attachment. Instead we copy the input stream
                    // onto the output stream which does automatically decode
                    // Base-64, quoted printable, and a variety of other formats.
                    int b;
                    while ((b = in.read()) != -1) {
                        out.write(b);
                    }
                    out.flush();
                }
            }
        } catch (IOException | MessagingException e) {
            log.error("Error occurred.", e);
        }
    }
}