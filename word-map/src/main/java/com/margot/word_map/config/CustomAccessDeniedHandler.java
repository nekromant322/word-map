package com.margot.word_map.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.margot.word_map.dto.CommonErrorDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");

        CommonErrorDto error = CommonErrorDto.builder()
                .code(HttpStatus.FORBIDDEN.value())
                .message("У вас нет доступа к этому ресурсу")
                .date(LocalDateTime.now())
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}
