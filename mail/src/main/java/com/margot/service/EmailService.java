package com.margot.service;

import com.margot.dto.ConfirmEmailRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String emailClient;

    public void sendConfirmEmail(ConfirmEmailRequest request) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(request.getEmail());
        message.setSubject("confirm code to word-map game");
        message.setText(request.getVerificationCode());
        message.setFrom(emailClient);

        try {
            javaMailSender.send(message);
        } catch (MailException e) {
            log.warn("send message to email {} error", request.getEmail(), e);
        }
    }
}
