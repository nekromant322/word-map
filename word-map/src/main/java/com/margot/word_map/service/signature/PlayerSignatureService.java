package com.margot.word_map.service.signature;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class PlayerSignatureService {
    private String hmacSecret;

    public String extractEmail(String json) throws JsonProcessingException {
        JsonNode node = new ObjectMapper().readTree(json);
        String email = node.path("email").asText();
        if (email == null || email.isBlank()) {
            throw new BadCredentialsException("Email отсутсвует");
        }
        return email;
    }

    public boolean isValid(String signature, String payload) throws NoSuchAlgorithmException, InvalidKeyException {
//        SecretKeySpec signingKey = new SecretKeySpec(hmacSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
//        Mac mac = Mac.getInstance("HmacSHA256");
//        mac.init(signingKey);
//        byte[] hashPayload = mac.doFinal(payload.trim().getBytes(StandardCharsets.UTF_8));
//        byte[] signatureBytes = Base64.getDecoder().decode(signature.trim());
//
//        return MessageDigest.isEqual(signatureBytes, hashPayload);
        return true; // временно
    }
}