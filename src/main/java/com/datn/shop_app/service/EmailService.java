package com.datn.shop_app.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    @Value("${spring.mail.from}")
    private String fromEmail;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendAccountDetails(String toEmail, String username, String password) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setReplyTo(fromEmail);
            helper.setSubject("Welcome, " + username + "! Your Account is Ready");

            //fix lại body sau khi có link login
            String emailContent = "<p>Dear user,</p>"
                    + "<p>Your account has been created successfully.</p>"
                    + "<p><b>Username:</b> " + username + "</p>"
                    + "<p><b>Password:</b> " + password + "</p>"
                    + "<p>Please log in and change your password after the first login.</p>"
                    + "<p>Best regards,<br>System Admin</p>";

            helper.setText(emailContent, true);
            mailSender.send(message);

            System.out.println("Email sent successfully to: " + toEmail);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMail(String toEmail, String subject, String content) throws MessagingException {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setReplyTo(fromEmail);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
    }

    public void sendMail(String fromMail, String toEmail, String subject, String content) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromMail);
        helper.setTo(toEmail);
        helper.setReplyTo(fromMail);
        helper.setSubject(subject);
        helper.setText(content, true);
        mailSender.send(message);
    }
}
