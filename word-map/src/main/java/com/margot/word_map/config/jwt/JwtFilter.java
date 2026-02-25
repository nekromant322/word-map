package com.margot.word_map.config.jwt;

import com.margot.word_map.exception.InvalidTokenException;
import com.margot.word_map.exception.UserNotAccessException;
import com.margot.word_map.service.auth.AdminDetailsService;
import com.margot.word_map.service.jwt.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final AdminDetailsService adminDetailsService;

    private final RequestAttributeSecurityContextRepository repo;

    private final HandlerExceptionResolver resolver;

    public JwtFilter(JwtService jwtService, AdminDetailsService adminDetailsService,
                     RequestAttributeSecurityContextRepository repo,
                     @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.jwtService = jwtService;
        this.adminDetailsService = adminDetailsService;
        this.repo = repo;
        this.resolver = resolver;
    }

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

                UserDetails details = adminDetailsService.loadUserByUsername(username);

                if (!details.isAccountNonLocked()) {
                    throw new UserNotAccessException("Аккаунт заблокирован: " + details.getUsername());
                }

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
            resolver.resolveException(request, response, null, new InvalidTokenException("Expired JWT token"));
        } catch (JwtException e) {
            resolver.resolveException(request, response, null, new InvalidTokenException());
        } catch (Exception e) {
            resolver.resolveException(request, response, null, e);
        }
    }
}
