package com.margot.word_map.service.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Profile("!prod")
public class LoggingEmailService implements EmailService {

    @Override
    public void sendConfirmEmail(String code, String email) {
        log.info("Тестовый код подтверждения [{}] отправлен на адрес: {}", code, email);
    }
}
