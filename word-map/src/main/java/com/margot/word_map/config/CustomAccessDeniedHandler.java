package com.margot.word_map.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.margot.word_map.exception.ErrorCode;
import com.margot.word_map.utils.ErrorResponseFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.io.IOException;
import java.util.Locale;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;
    private final ErrorResponseFactory errorResponseFactory;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException ex) throws IOException, ServletException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");

        Locale locale = RequestContextUtils.getLocale(request);
        objectMapper.writeValue(
                response.getOutputStream(),
                errorResponseFactory.build(ErrorCode.USER_NOT_PERMISSIONS, locale));

        log.warn(ex.getMessage(), ex);
    }
}
