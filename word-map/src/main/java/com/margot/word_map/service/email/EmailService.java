package com.margot.word_map.service.email;

public interface EmailService {

    void sendConfirmEmail(String code, String email);
}
