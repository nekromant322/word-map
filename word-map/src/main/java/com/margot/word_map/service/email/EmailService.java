package com.margot.word_map.service.email;

import com.margot.word_map.config.SenderProperties;
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

    public void sendConfirmEmail(String code, String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@word-map.ru");
        message.setTo(email);
        message.setSubject("Word Map: confirm code to admin panel");
        message.setText("Ваш код подтверждения для входа в административную панель Word Map: " + code +
                "\n\nЕсли это были не вы, проигнорируйте это письмо.");
        message.setFrom(senderProperties.getUsername());

        if (needConfirm) {
            try {
                javaMailSender.send(message);
                log.info("send confirm code message to email {}", email);
            } catch (MailException e) {
                log.warn("send message to email {} error", email, e);
            }
        } else {
            System.out.println("send confirm code message to email: " + email);
        }
    }
}
