package com.margot.word_map.service.signature;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class PlayerSignatureService {
    private final ObjectMapper objectMapper;

    public String extractUuid(String header) throws JacksonException {
        String payload = header.split("\\.")[1];
        byte[] decodedPayload = Base64.getDecoder().decode(payload);
        String json = new String(decodedPayload, StandardCharsets.UTF_8);
        JsonNode node = objectMapper.readTree(json);
        String uuid = node.path("data").path("id").asText();
        if (uuid == null || uuid.isBlank()) {
            throw new BadCredentialsException("Uuid отсутствует");
        }
        return uuid;
    }

    public boolean isValid(String signature) {
        // проверка подписи игрока
        return true; // временно
    }
}