package com.margot.word_map.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.margot.word_map.exception.ErrorCode;
import com.margot.word_map.utils.ErrorResponseFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.io.IOException;
import java.util.Locale;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ErrorResponseFactory errorResponseFactory;
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException ex) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");

        Locale locale = RequestContextUtils.getLocale(request);
        objectMapper.writeValue(
                response.getOutputStream(),
                errorResponseFactory.build(ErrorCode.UNAUTHORIZED, locale));

        log.warn(ex.getMessage(), ex);
    }
}