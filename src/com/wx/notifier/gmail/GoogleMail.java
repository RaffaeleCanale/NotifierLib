package com.wx.notifier.gmail;

import com.sun.mail.smtp.SMTPTransport;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.security.Security;
import java.util.Date;
import java.util.Properties;

/**
 * @author doraemon
 */
public class GoogleMail {
    private GoogleMail() {
    }

    /**
     * Send email using GMail SMTP server.
     *
     * @param username       GMail username
     * @param password       GMail password
     * @param recipientEmail TO recipient
     * @param title          title of the message
     * @param message        message to be sent
     * @param attachments    (Optional) File attachments
     *
     * @throws AddressException   if the email address parse failed
     * @throws MessagingException if the connection is dead or not in the connected state or if the message is not a
     *                            MimeMessage
     */
    public static void Send(final String username, final String password, String recipientEmail, String title, String message, File... attachments) throws MessagingException {
        GoogleMail.Send(username, password, recipientEmail, "", title, message, attachments);
    }



    /**
     * Send email using GMail SMTP server.
     *
     * @param username GMail username
     * @param password GMail password
     * @param recipientEmail TO recipient
     * @param ccEmail CC recipient. Can be empty if there is no CC recipient
     * @param title title of the message
     * @param message message to be sent
     * @throws AddressException if the email address parse failed
     * @throws MessagingException if the connection is dead or not in the connected state or if the message is not a MimeMessage
     */
    // TODO: 3/2/16 Remove this when the below method is tested correct in both cases (with/without attachment)
//    public static void Send(final String username, final String password, String recipientEmail, String ccEmail, String title, String message) throws AddressException, MessagingException {
//        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
//        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
//
//        // Get a Properties object
//        Properties props = System.getProperties();
//        props.setProperty("mail.smtps.host", "smtp.gmail.com");
//        props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
//        props.setProperty("mail.smtp.socketFactory.fallback", "false");
//        props.setProperty("mail.smtp.port", "465");
//        props.setProperty("mail.smtp.socketFactory.port", "465");
//        props.setProperty("mail.smtps.auth", "true");
//
//        /*
//        If set to false, the QUIT command is sent and the connection is immediately closed. If set
//        to true (the default), causes the transport to wait for the response to the QUIT command.
//
//        ref :   http://java.sun.com/products/javamail/javadocs/com/sun/mail/smtp/package-summary.html
//                http://forum.java.sun.com/thread.jspa?threadID=5205249
//                smtpsend.java - demo program from javamail
//        */
//        props.put("mail.smtps.quitwait", "false");
//
//        Session session = Session.getInstance(props, null);
//
//        // -- Create a new message --
//        final MimeMessage msg = new MimeMessage(session);
//
//        // -- Set the FROM and TO fields --
//        msg.setFrom(new InternetAddress(username + "@gmail.com"));
//        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail, false));
//
//        if (ccEmail.length() > 0) {
//            msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccEmail, false));
//        }
//
//        msg.setSubject(title);
//        msg.setText(message, "utf-8");
//        msg.setSentDate(new Date());
//
//        SMTPTransport t = (SMTPTransport)session.getTransport("smtps");
//
//        t.connect("smtp.gmail.com", username, password);
//        t.sendMessage(msg, msg.getAllRecipients());
//        t.close();
//    }


    /**
     * Send email using GMail SMTP server.
     *
     * @param username       GMail username
     * @param password       GMail password
     * @param recipientEmail TO recipient
     * @param ccEmail        CC recipient. Can be empty if there is no CC recipient
     * @param title          title of the message
     * @param message        message to be sent
     * @param attachments    (Optional) File attachments
     *
     * @throws AddressException   if the email address parse failed
     * @throws MessagingException if the connection is dead or not in the connected state or if the message is not a
     *                            MimeMessage     *
     */
    public static void Send(final String username, final String password, String recipientEmail, String ccEmail, String title, String message, File... attachments) throws MessagingException {
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

        // Get a Properties object
        Properties props = System.getProperties();
        props.setProperty("mail.smtps.host", "smtp.gmail.com");
        props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.port", "465");
        props.setProperty("mail.smtp.socketFactory.port", "465");
        props.setProperty("mail.smtps.auth", "true");

        /*
        If set to false, the QUIT command is sent and the connection is immediately closed. If set
        to true (the default), causes the transport to wait for the response to the QUIT command.

        ref :   http://java.sun.com/products/javamail/javadocs/com/sun/mail/smtp/package-summary.html
                http://forum.java.sun.com/thread.jspa?threadID=5205249
                smtpsend.java - demo program from javamail
        */
        props.put("mail.smtps.quitwait", "false");

        Session session = Session.getInstance(props, null);

        // -- Create a new message --
        final MimeMessage msg = new MimeMessage(session);

        // -- Set the FROM and TO fields --
        msg.setFrom(new InternetAddress(username + "@gmail.com"));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail, false));

        if (ccEmail.length() > 0) {
            msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccEmail, false));
        }

        msg.setSubject(title);
        msg.setSentDate(new Date());

        // Create the message part
        BodyPart messageBodyPart = new MimeBodyPart();

        // Now set the actual message
        messageBodyPart.setText(message);


        // Create a multipar message
        Multipart multipart = new MimeMultipart();
        // Set text message part
        multipart.addBodyPart(messageBodyPart);

        // Part two is attachment
        if (attachments != null && attachments.length > 0) {
            for (File file : attachments) {
                BodyPart filePart = new MimeBodyPart();
                DataSource source = new FileDataSource(file);
                filePart.setDataHandler(new DataHandler(source));
                filePart.setFileName(file.getName());
                multipart.addBodyPart(filePart);
            }
        }


        // Send the complete message parts
        msg.setContent(multipart);

        SMTPTransport t = (SMTPTransport) session.getTransport("smtps");

        t.connect("smtp.gmail.com", username, password);
        t.sendMessage(msg, msg.getAllRecipients());
        t.close();
    }
}
