package com.margot.word_map.controller;

import com.margot.word_map.dto.ConfirmEmailRequest;
import com.margot.word_map.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mail")
public class ConfirmEmailController {

    private final EmailService emailService;

    @PostMapping
    public void sendConfirmEmailMessage(@RequestBody ConfirmEmailRequest request) {
        emailService.sendConfirmEmail(request);
    }
}
