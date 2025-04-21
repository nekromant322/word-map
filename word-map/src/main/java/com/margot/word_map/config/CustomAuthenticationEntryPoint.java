package com.margot.word_map.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.margot.word_map.dto.CommonErrorDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        CommonErrorDto error = CommonErrorDto.builder()
                .code(HttpStatus.UNAUTHORIZED.value())
                .message("Full authentication is required")
                .date(LocalDateTime.now())
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}