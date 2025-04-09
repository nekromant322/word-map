package com.margot.word_map.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.margot.word_map.dto.CommonErrorDto;
import com.margot.word_map.service.jwt.JwtService;
import com.margot.word_map.service.auth.AdminDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final AdminDetailsService myUserDetailsService;

    private final RequestAttributeSecurityContextRepository repo;

    private final ObjectMapper om;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                username = jwtService.extractUserName(token);
            }
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails details = myUserDetailsService.loadUserByUsername(username);

                if (jwtService.validateToken(token, details)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    SecurityContext context = SecurityContextHolder.getContext();
                    repo.saveContext(context, request, response);
                }
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            log.error("expired jwt token:", e);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(
                    om.writeValueAsString(
                            CommonErrorDto.builder()
                                    .code(HttpStatus.UNAUTHORIZED.value())
                                    .message("expired jwt token")
                                    .date(LocalDateTime.now())
                                    .build()
                    )
            );
        } catch (JwtException e) {
            log.error("invalid jwt token:", e);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(
                    om.writeValueAsString(
                            CommonErrorDto.builder()
                                    .code(HttpStatus.UNAUTHORIZED.value())
                                    .message("invalid jwt token")
                                    .date(LocalDateTime.now())
                                    .build()
                    )
            );
        }
    }
}
