package com.isa.java.mail.basic.event;

import com.isa.java.mail.basic.util.ApplicationProperties.GmailImapProperties;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;
import java.io.IOException;
import java.util.Properties;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Preconditions;

@Slf4j
public class MonitorGmail {

    private static final String PROTOCOL = "imap";
    private static final String FOLDER = "INBOX";
    private static final int FREQUENCY = 1000;
    private static final GmailImapProperties IMAP_PROPERTIES = new GmailImapProperties();

    public static void main(String[] args) {
        log.info("Monitoring has begun");

        // Get session with properties
        Properties properties = new Properties();
        properties.setProperty("mail.imap.ssl.enable", "true");
        Session session = Session.getInstance(properties);

        IMAPStore store = null;
        Folder inbox = null;
        try {
            //Get store
            store = (IMAPStore) session.getStore(PROTOCOL);
            store.connect(IMAP_PROPERTIES.getHost(), IMAP_PROPERTIES.getUsername(),
                    IMAP_PROPERTIES.getPassword());

            // Get folder
            inbox = store.getFolder(FOLDER);
            Preconditions.checkArgument(inbox != null && inbox.exists(), "Invalid folder");

            inbox.open(Folder.READ_ONLY);
            log.info("Opened the folder");
            inbox.addMessageCountListener(new MessageCountAdapter() {
                @Override
                public void messagesAdded(MessageCountEvent ev) {
                    Message[] msgs = ev.getMessages();
                    log.info("Got " + msgs.length + " new messages");

                    // Just dump out the new messages
                    for (int i = 0; i < 5; i++) {
                        try {
                            log.info("-----");
                            log.info("Message " + msgs[i].getMessageNumber() + ":");
                            msgs[i].writeTo(System.out);
                        } catch (IOException | MessagingException e) {
                            log.error("Error occurred.", e);
                        }
                    }
                }
            });

            boolean supportsIdle = store.hasCapability("IDLE");

            while (true) {
                if (supportsIdle && inbox instanceof IMAPFolder) {
                    IMAPFolder f = (IMAPFolder) inbox;
                    f.idle();
                    log.info("IDLE done");
                } else {
                    log.info("Sleeping");
                    Thread.sleep(FREQUENCY); // sleep for FREQUENCY milliseconds

                    // This is to force the IMAP server to send us
                    // EXISTS notifications.
                    inbox.getMessageCount();
                }
            }
        } catch (Exception e) {
            log.error("Error occurred.", e);
        } finally {
            close(inbox);
            close(store);
        }
    }

    public static void close(final Folder folder) {
        try {
            if (folder != null && folder.isOpen()) {
                folder.close(false);
            }
        } catch (final Exception e) {
            log.error("Error occurred when closing folder.", e);
        }
    }

    public static void close(final Store store) {
        try {
            if (store != null && store.isConnected()) {
                store.close();
            }
        } catch (final Exception e) {
            log.error("Error occurred when closing store.", e);
        }
    }
}