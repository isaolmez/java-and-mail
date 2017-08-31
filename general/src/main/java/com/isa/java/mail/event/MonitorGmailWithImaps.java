package com.isa.java.mail.event;

import com.isa.java.mail.util.ApplicationProperties.GmailImapProperties;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;
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
public class MonitorGmailWithImaps {

    private static final String PROTOCOL = "imaps";
    private static final String FOLDER = "INBOX";
    private static final GmailImapProperties IMAP_PROPERTIES = new GmailImapProperties();

    public static void main(String[] args) {
        // Get session with properties
        Properties properties = new Properties();
        properties.setProperty("mail.store.protocol", PROTOCOL);
        properties.setProperty("mail.imaps.host", IMAP_PROPERTIES.getHost());
        properties.setProperty("mail.imaps.port", IMAP_PROPERTIES.getPortAsString());
        properties.setProperty("mail.imaps.timeout", IMAP_PROPERTIES.getTimeOut());
        Session session = Session.getInstance(properties);

        IMAPStore store = null;
        Folder inbox = null;
        try {
            // Get store
            store = (IMAPStore) session.getStore(PROTOCOL);
            store.connect(IMAP_PROPERTIES.getUsername(), IMAP_PROPERTIES.getPassword());
            if (!store.hasCapability("IDLE")) {
                throw new RuntimeException("IDLE not supported");
            }

            // Get folder
            inbox = store.getFolder(FOLDER);
            Preconditions.checkArgument(inbox != null && inbox.exists(), "Invalid folder");

            inbox.addMessageCountListener(new MessageCountAdapter() {

                @Override
                public void messagesAdded(MessageCountEvent event) {
                    Message[] messages = event.getMessages();

                    for (Message message : messages) {
                        try {
                            log.info("Mail Subject:- " + message.getSubject());
                        } catch (MessagingException e) {
                            log.error("Error occurred.", e);
                        }
                    }
                }
            });

            IdleThread idleThread = new IdleThread(inbox);
            idleThread.setDaemon(false);
            idleThread.start();

            idleThread.join();
            // idleThread.kill(); //to terminate from another thread

        } catch (Exception e) {
            log.error("Error occurred", e);
        } finally {
            close(inbox);
            close(store);
        }
    }

    private static class IdleThread extends Thread {

        private final Folder folder;
        private volatile boolean running = true;

        public IdleThread(Folder folder) {
            this.folder = folder;
        }

        public synchronized void kill() {
            if (!running) {
                return;
            }

            this.running = false;
        }

        @Override
        public void run() {
            while (running) {

                try {
                    ensureOpen(folder);
                    log.info("Enter idle");
                    ((IMAPFolder) folder).idle();
                } catch (Exception e) {
                    // something went wrong
                    // wait and try again
                    log.error("Error occurred.", e);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ie) {
                        log.error("Interrupted.", ie);
                    }
                }
            }
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

    public static void ensureOpen(final Folder folder) throws MessagingException {

        if (folder != null) {
            Store store = folder.getStore();
            if (store != null && !store.isConnected()) {
                store.connect(IMAP_PROPERTIES.getUsername(), IMAP_PROPERTIES.getPassword());
            }
        } else {
            throw new MessagingException("Unable to open a null folder");
        }

        if (folder.exists()
                && !folder.isOpen()
                && (folder.getType() & Folder.HOLDS_MESSAGES) != 0) {
            log.info("Open folder " + folder.getFullName());
            folder.open(Folder.READ_ONLY);
            if (!folder.isOpen()) {
                throw new MessagingException("Unable to open folder " + folder.getFullName());
            }
        }
    }
}
