package com.ironbank.money_transfer.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final JavaMailSender mailSender;

    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendWelcomeEmail(String toEmail, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Welcome to The Iron Bank of Sri Lanka 🏦");
        message.setText("Dear " + username + ",\n\n" +
                "Your account has been successfully created.\n" +
                "You can now login and start transferring millions!\n\n" +
                "Securely yours,\n" +
                "The Iron Bank Team");

        mailSender.send(message);
        System.out.println("📧 Mail sent to " + toEmail);
    }
}