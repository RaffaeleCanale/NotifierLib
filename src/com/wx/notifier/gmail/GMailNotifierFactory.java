package com.wx.notifier.gmail;


import com.wx.console.UserConsoleInterface;
import com.wx.crypto.Crypter;
import com.wx.crypto.CryptoException;
import com.wx.notifier.Notifier;
import com.wx.notifier.NotifierFactory;
import com.wx.properties.page.ResourcePage;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Created on 27/03/2015
 *
 * @author Raffaele Canale (raffaelecanale@gmail.com)
 * @version 1.0
 */
public class GMailNotifierFactory implements NotifierFactory {

    private static final String KEY_USER = "gmail.user";
    private static final String KEY_DESTINATION = "gmail.destination";
    private static final String KEY_PASSWORD = "gmail.password";
    private static final String KEY_ENCRYPT_CREDS = "gmail.encrypt_all";

    @Override
    public void inputAndStore(UserConsoleInterface in, ResourcePage config, Crypter crypter) throws CryptoException, IOException {
        in.getConsole().println("GMail notifier setup.\n" +
                "To setup this notifier, you'll need to provide a Google account and password (used as the sender) and a destination email address.");


        String gmailUser = in.readLine("Enter your GMail user: ");
        char[] gmailPassword = in.readPassword("Enter your GMail password: ");
        String destination = in.readLine("Enter the destination e-mail address: ");
        boolean encryptAll = false;
        if (crypter != null) {
            in.getConsole().print("The Google account password will be encrypted for security reasons.\n" +
                    "Do you wish to encrypt the user and destination address as well? ");
            encryptAll = in.inputYesNo();
        }

        setProperty(config, KEY_USER, gmailUser, encryptAll ? crypter : null);
        setProperty(config, KEY_DESTINATION, destination, encryptAll ? crypter : null);
        setProperty(config, KEY_PASSWORD, new String(gmailPassword), crypter);
        config.setProperty(KEY_ENCRYPT_CREDS, encryptAll);
    }

    private void setProperty(ResourcePage config, String key, String value, Crypter crypter) throws CryptoException {
        if (crypter != null) {
            byte[] data = crypter.encrypt(value.getBytes(StandardCharsets.UTF_8));
            config.setProperty(key, data);
        } else {
            config.setProperty(key, value);
        }
    }

    @Override
    public GMailNotifier loadFrom(ResourcePage config, Crypter crypter) throws CryptoException, IOException {

        Optional<Boolean> encryptAll = config.getBoolean(KEY_ENCRYPT_CREDS);
        if (!encryptAll.isPresent()) {
            return null;
        }

        String gmailUser = getProperty(config, KEY_USER, encryptAll.get() ? crypter : null);
        String destination = getProperty(config, KEY_DESTINATION, encryptAll.get() ? crypter : null);
        String gmailPassword = getProperty(config, KEY_PASSWORD, crypter);

        return loadFrom(gmailUser, gmailPassword, destination);
    }

    private String getProperty(ResourcePage config, String key, Crypter crypter) throws CryptoException, IOException {
        if (crypter != null) {
            byte[] data = config.getBytes(key)
                    .orElseThrow(() -> new IOException("Missing key " + key));

            return new String(crypter.decrypt(data), StandardCharsets.UTF_8);

        } else {
            return config.getString(key)
                    .orElseThrow(() -> new IOException("Missing key " + key));
        }
    }

    public GMailNotifier loadFrom(String gmailUser, String gmailPassword, String destination) {
        return new GMailNotifier(gmailPassword, gmailUser, destination);
    }

    public static class GMailNotifier implements Notifier {
        private final String gmailPassword;
        private final String gmailUser;
        private final String destination;

        public GMailNotifier(String gmailPassword, String gmailUser, String destination) {
            this.gmailPassword = gmailPassword;
            this.gmailUser = gmailUser;
            this.destination = destination;
        }

        @Override
        public void notify(String title, String message) throws IOException {
            try {
                GoogleMail.Send(gmailUser, gmailPassword, destination, title, message);
            } catch (MessagingException e) {
                throw new IOException(e);
            }
        }

        public void notify(String title, String message, File... attachments) throws IOException {
            try {
                GoogleMail.Send(gmailUser, gmailPassword, destination, title, message, attachments);
            } catch (MessagingException e) {
                throw new IOException(e);
            }
        }

        @Override
        public String toString() {
            return "GMail[" + gmailUser + "]";
        }
    }
}
