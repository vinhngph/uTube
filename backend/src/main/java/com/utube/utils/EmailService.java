package com.utube.utils;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class EmailService {
    public static void sendEmail(String to, String subject, String body) {
        final String email = Config.getProperty("EMAIL_ADDRESS");
        final String password = Config.getProperty("EMAIL_TOKEN");

        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true"); // TLS

        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, password);
            }
        };

        Session session = Session.getInstance(properties, authenticator);
        try {
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(email));
            message.setHeader("Content-Type", "text/HTML; charset=UTF-8");

            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            message.setSubject(subject);

            message.setContent(body, "text/html; charset=utf-8");

            Transport.send(message);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
}
