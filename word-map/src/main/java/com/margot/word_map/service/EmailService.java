package com.margot.word_map.service;

import com.margot.word_map.config.SenderProperties;
import com.margot.word_map.dto.request.ConfirmEmailRequest;
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

    private final SenderProperties senderProperties;

    @Value("${auth.confirm-by-email}")
    private Boolean needConfirm;

    public void sendConfirmEmail(ConfirmEmailRequest request) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(request.getEmail());
        message.setSubject("confirm code to word-map game");
        message.setText("Ваш код подтверждения для входа в word-map: " + request.getVerificationCode());
        message.setFrom(senderProperties.getUsername());

        if (needConfirm) {
            try {
                javaMailSender.send(message);
                log.info("send confirm code message to email {}", request.getEmail());
            } catch (MailException e) {
                log.warn("send message to email {} error", request.getEmail(), e);
            }
        } else {
            System.out.println("send confirm code message to email: " + request.getEmail());
        }
    }
}
