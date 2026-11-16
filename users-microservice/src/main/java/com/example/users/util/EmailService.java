package com.example.users.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService implements EmailSender {

	private final JavaMailSender mailSender;

	// l'expéditeur est l'adresse Gmail de application.properties
	@Value("${spring.mail.username}")
	private String from;

	public EmailService(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	@Override
	public void sendEmail(String to, String email) {
		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
			helper.setText(email, true);
			helper.setTo(to);
			helper.setSubject("Confirm your email");
			helper.setFrom(from);
			mailSender.send(mimeMessage);
		} catch (MessagingException e) {
			throw new IllegalStateException("failed to send email");
		}
	}
}
